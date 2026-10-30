# Astra Anime Client — помощник «Сейчас играет».
# Читает системные медиа-сессии Windows (GSMTC: Spotify, Яндекс Музыка, браузеры и т.д.)
# и печатает состояние в stdout строками JSON. Команды читает из stdin:
#   play | pause | toggle | next | prev | seek <мс> | quit
# Обложка сохраняется как PNG в папку, переданную первым аргументом.
param([string]$CoverDir = $env:TEMP)

$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
Add-Type -AssemblyName System.Runtime.WindowsRuntime
Add-Type -AssemblyName System.Drawing

$asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
    $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and
    $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]

function Await($op, [Type]$type) {
    $task = $asTaskGeneric.MakeGenericMethod($type).Invoke($null, @($op))
    if (-not $task.Wait(3000)) { return $null }
    return $task.Result
}

[void][Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType = WindowsRuntime]
[void][Windows.Storage.Streams.IRandomAccessStreamWithContentType, Windows.Storage.Streams, ContentType = WindowsRuntime]
$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]
$propsType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]
$streamType = [Windows.Storage.Streams.IRandomAccessStreamWithContentType]

$mgr = Await ($mgrType::RequestAsync()) $mgrType
if ($null -eq $mgr) { '{"error":"no-manager"}'; exit 1 }

function Esc([string]$s) {
    if ($null -eq $s) { return '' }
    return ($s -replace '\\', '\\' -replace '"', '\"' -replace "`r", '' -replace "`n", ' ' -replace "`t", ' ')
}

function Save-Cover($props, [string]$key) {
    try {
        if ($null -eq $props.Thumbnail) { return '' }
        $hash = [BitConverter]::ToString([Security.Cryptography.MD5]::Create().ComputeHash(
            [Text.Encoding]::UTF8.GetBytes($key))).Replace('-', '').Substring(0, 16)
        $file = Join-Path $CoverDir "cover_$hash.png"
        if (Test-Path $file) { return $file }
        $ras = Await ($props.Thumbnail.OpenReadAsync()) $streamType
        if ($null -eq $ras) { return '' }
        $net = [System.IO.WindowsRuntimeStreamExtensions]::AsStreamForRead($ras)
        $mem = New-Object System.IO.MemoryStream
        $net.CopyTo($mem); $net.Dispose()
        $mem.Position = 0
        $img = [System.Drawing.Image]::FromStream($mem)
        # Spotify и браузеры иногда отдают обложку с полями — обрезаем до квадрата по центру
        $side = [Math]::Min($img.Width, $img.Height)
        $size = [Math]::Min(256, $side)
        $bmp = New-Object System.Drawing.Bitmap $size, $size
        $g = [System.Drawing.Graphics]::FromImage($bmp)
        $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $src = New-Object System.Drawing.Rectangle ([int](($img.Width - $side) / 2)), ([int](($img.Height - $side) / 2)), $side, $side
        $g.DrawImage($img, (New-Object System.Drawing.Rectangle 0, 0, $size, $size), $src, [System.Drawing.GraphicsUnit]::Pixel)
        $g.Dispose(); $img.Dispose(); $mem.Dispose()
        $tmp = "$file.tmp"
        $bmp.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png); $bmp.Dispose()
        Move-Item -Force $tmp $file
        return $file
    } catch { return '' }
}

# --- запасной режим: браузеры, которые не передают трек в Windows (например, Яндекс Браузер).
# Название берётся из заголовка окна, управление — системными мультимедиа-клавишами.
Add-Type -Namespace Astra -Name Keys -MemberDefinition '[DllImport("user32.dll")] public static extern void keybd_event(byte vk, byte scan, uint flags, System.UIntPtr extra);'
function Send-MediaKey([byte]$vk) {
    [Astra.Keys]::keybd_event($vk, 0, 0, [UIntPtr]::Zero)
    [Astra.Keys]::keybd_event($vk, 0, 2, [UIntPtr]::Zero)
}
$browserNames = @('browser', 'chrome', 'msedge', 'firefox', 'opera', 'brave', 'vivaldi')
$browserSuffix = '[\s\u00A0]+[-—–][\s\u00A0]+(Яндекс[\s\u00A0]Браузер|Yandex[\s\u00A0]Browser|Yandex|Google[\s\u00A0]Chrome|Microsoft.?Edge|Mozilla[\s\u00A0]Firefox|Opera|Brave|Vivaldi)[\s\u00A0]*$'
$winKey = ''; $winSince = 0
$seen = @{}            # трек -> когда начался (чтобы позиция не сбрасывалась при смене вкладки)
$lastBt = $null; $lastBtAt = 0
$pausedAt = 0; $spotifyPaused = $false

Add-Type -Namespace Astra -Name Windows -MemberDefinition @'
delegate bool EnumProc(System.IntPtr h, System.IntPtr l);
[DllImport("user32.dll")] static extern bool EnumWindows(EnumProc p, System.IntPtr l);
[DllImport("user32.dll", CharSet = CharSet.Unicode)] static extern int GetWindowText(System.IntPtr h, System.Text.StringBuilder s, int n);
[DllImport("user32.dll")] static extern uint GetWindowThreadProcessId(System.IntPtr h, out uint pid);
[DllImport("user32.dll")] static extern bool IsWindowVisible(System.IntPtr h);
// Заголовки всех видимых окон процессов: "pid	заголовок". MainWindowTitle у браузеров часто устаревший.
public static System.Collections.Generic.List<string> Titles(uint[] pids) {
    var r = new System.Collections.Generic.List<string>();
    var set = new System.Collections.Generic.HashSet<uint>(pids);
    EnumWindows((h, l) => {
        uint pid; GetWindowThreadProcessId(h, out pid);
        if (set.Contains(pid) && IsWindowVisible(h)) {
            var sb = new System.Text.StringBuilder(512); GetWindowText(h, sb, 512);
            if (sb.Length > 0) r.Add(pid + "	" + sb.ToString());
        }
        return true;
    }, System.IntPtr.Zero);
    return r;
}
'@

function Find-BrowserTrack {
    $procs = @(Get-Process -Name $browserNames -ErrorAction SilentlyContinue)
    if ($procs.Count -eq 0) { return $null }
    $names = @{}
    foreach ($p in $procs) { $names[[uint32]$p.Id] = $p.ProcessName }
    foreach ($entry in [Astra.Windows]::Titles([uint32[]]$names.Keys)) {
        $tab = $entry.IndexOf("`t")
        $proc = @{ ProcessName = $names[[uint32]$entry.Substring(0, $tab)] }
        $t = $entry.Substring($tab + 1)
        $t = ($t -replace $browserSuffix, '').Trim()
        $title = ''; $artist = ''
        if ($t -match '^(.+?)\s+•\s+(.+)$') {
            # Яндекс Музыка / Spotify Web: «Песня • Исполнитель»
            $title = $matches[1].Trim(); $artist = $matches[2].Trim()
        } elseif ($t -match '^(?:\(\d+\)\s*)?(.+?)\s+[-—]\s+YouTube(?: Music)?$') {
            $v = $matches[1].Trim()
            if ($v -match '^(.+?)\s+[-—–]\s+(.+)$') { $artist = $matches[1].Trim(); $title = $matches[2].Trim() } else { $title = $v }
        } else {
            # Spotify Web на паузе показывает заголовок по умолчанию
            if ($t -match '^Spotify\s*[-—–]\s*Web Player') { $script:spotifyPaused = $true }
            continue
        }
        if ($title -ne '') { return @{ app = "$($proc.ProcessName).exe"; title = $title; artist = $artist } }
    }
    return $null
}

$stdin = New-Object System.IO.StreamReader([Console]::OpenStandardInput(), [Text.Encoding]::UTF8)
$pending = $stdin.ReadLineAsync()
$lastKey = ''; $lastProps = $null; $cover = ''
$tick = 0

while ($true) {
    # --- команды
    while ($pending.IsCompleted) {
        $cmd = $pending.Result
        if ($null -eq $cmd -or $cmd -eq 'quit') { exit 0 }
        $pending = $stdin.ReadLineAsync()
        try {
            $s = $mgr.GetCurrentSession()
            if ($null -eq $s -and $winKey -ne '') {
                switch ($cmd.Trim().Split(' ')[0]) {
                    'toggle' { Send-MediaKey 0xB3 }
                    'play'   { Send-MediaKey 0xB3 }
                    'pause'  { Send-MediaKey 0xB3 }
                    'next'   { Send-MediaKey 0xB0 }
                    'prev'   { Send-MediaKey 0xB1 }
                    'setpos' {
                        $winSince = [DateTimeOffset]::Now.ToUnixTimeMilliseconds() - [long]$cmd.Trim().Split(' ')[1]
                        $seen[$winKey] = $winSince
                    }
                }
                $tick = 99
            }
            if ($null -ne $s) {
                $parts = $cmd.Trim().Split(' ')
                $boolType = [bool]
                switch ($parts[0]) {
                    'play'   { [void](Await ($s.TryPlayAsync()) $boolType) }
                    'pause'  { [void](Await ($s.TryPauseAsync()) $boolType) }
                    'toggle' { [void](Await ($s.TryTogglePlayPauseAsync()) $boolType) }
                    'next'   { [void](Await ($s.TrySkipNextAsync()) $boolType) }
                    'prev'   { [void](Await ($s.TrySkipPreviousAsync()) $boolType) }
                    'seek'   { [void](Await ($s.TryChangePlaybackPositionAsync([long]$parts[1] * 10000)) $boolType) }
                }
                $tick = 99 # сразу отправить свежее состояние
            }
        } catch { }
    }

    # --- состояние (раз в ~500 мс, команды проверяются каждые 50 мс)
    $tick++
    if ($tick -ge 10) {
        $tick = 0
        try {
            $s = $mgr.GetCurrentSession()
            if ($null -eq $s) {
                $spotifyPaused = $false
                $bt = Find-BrowserTrack
                $now = [DateTimeOffset]::Now.ToUnixTimeMilliseconds()
                $status = 'Playing'
                if ($null -ne $bt) {
                    # снова играет тот же трек после паузы — время паузы не считаем
                    if ($pausedAt -gt 0 -and "$($bt.title)|$($bt.artist)" -eq $winKey) { $winSince += $now - $pausedAt; $seen[$winKey] = $winSince }
                    $pausedAt = 0
                    $lastBt = $bt; $lastBtAt = $now
                } elseif ($null -ne $lastBt -and $spotifyPaused -and $now - $lastBtAt -lt 1800000) {
                    # пауза в Spotify Web: держим трек и замораживаем позицию
                    if ($pausedAt -eq 0) { $pausedAt = $now }
                    $bt = $lastBt; $status = 'Paused'
                } elseif ($null -ne $lastBt -and $now - $lastBtAt -lt 8000) { $bt = $lastBt } # короткая смена вкладки — не теряем трек
                if ($null -eq $bt) {
                    $winKey = ''
                    '{"active":false}'
                } else {
                    $k = "$($bt.title)|$($bt.artist)"
                    # позиция неизвестна — считаем от момента, когда трек впервые появился в заголовке;
                    # вернулись к тому же треку (переключали вкладку) — продолжаем с прежнего места
                    if ($k -ne $winKey) {
                        $winKey = $k
                        if ($seen.ContainsKey($k) -and $now - $seen[$k] -lt 900000) { $winSince = $seen[$k] }
                        else { $winSince = $now; $seen[$k] = $now }
                    }
                    $posNow = if ($pausedAt -gt 0) { $pausedAt - $winSince } else { $now - $winSince }
                    '{"active":true,"source":"window","app":"' + (Esc $bt.app) + '","title":"' + (Esc $bt.title) + '","artist":"' + (Esc $bt.artist) +
                    '","album":"","status":"' + $status + '","pos":' + $posNow + ',"dur":0,"age":0,"cover":"","canNext":true,"canPrev":true,"canSeek":false}'
                }
            } else {
                $app = $s.SourceAppUserModelId
                $props = Await ($s.TryGetMediaPropertiesAsync()) $propsType
                $title = ''; $artist = ''; $album = ''
                if ($null -ne $props) { $title = $props.Title; $artist = $props.Artist; $album = $props.AlbumTitle }
                $key = "$app|$title|$artist"
                if ($key -ne $lastKey) { $lastKey = $key; $cover = '' ; $coverTries = 0 }
                # обложка часто приходит на секунду позже названия — пробуем несколько раз
                if ($cover -eq '' -and $coverTries -lt 6 -and $null -ne $props) { $coverTries++; $cover = Save-Cover $props $key }
                $tl = $s.GetTimelineProperties()
                $pb = $s.GetPlaybackInfo()
                $status = "$($pb.PlaybackStatus)"
                $pos = [long]$tl.Position.TotalMilliseconds
                $dur = [long]($tl.EndTime - $tl.StartTime).TotalMilliseconds
                $age = [long]([DateTimeOffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds
                $caps = $pb.Controls
                '{"active":true,"app":"' + (Esc $app) + '","title":"' + (Esc $title) + '","artist":"' + (Esc $artist) +
                    '","album":"' + (Esc $album) + '","status":"' + $status + '","pos":' + $pos + ',"dur":' + $dur +
                    ',"age":' + $age + ',"cover":"' + (Esc $cover) + '","canNext":' + "$($caps.IsNextEnabled)".ToLower() +
                    ',"canPrev":' + "$($caps.IsPreviousEnabled)".ToLower() + ',"canSeek":' + "$($caps.IsPlaybackPositionEnabled)".ToLower() + '}'
            }
        } catch {
            '{"active":false,"error":"' + (Esc $_.Exception.Message) + '"}'
        }
        [Console]::Out.Flush()
    }
    Start-Sleep -Milliseconds 50
}
