/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  javazoom.jl.decoder.Bitstream
 *  javazoom.jl.decoder.Decoder
 *  javazoom.jl.decoder.Header
 *  javazoom.jl.decoder.SampleBuffer
 *  net.minecraft.client.sounds.FloatSampleSource
 *  net.minecraft.client.sounds.JOrbisAudioStream
 */
package astra.sound.user;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.minecraft.client.sounds.FloatSampleSource;
import net.minecraft.client.sounds.JOrbisAudioStream;

public final class Decoders {
    public static final String[] EXTENSIONS = new String[]{"ogg", "mp3", "wav"};

    private Decoders() {
    }

    public static boolean supported(Path file) {
        String ext = Decoders.extension(file);
        for (String e : EXTENSIONS) {
            if (!e.equals(ext)) continue;
            return true;
        }
        return false;
    }

    public static String extension(Path file) {
        String n = file.getFileName().toString();
        int dot = n.lastIndexOf(46);
        return dot < 0 ? "" : n.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    public static FloatSampleSource open(Path file) throws IOException {
        BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file, new OpenOption[0]), 65536);
        try {
            return switch (Decoders.extension(file)) {
                case "ogg" -> new JOrbisAudioStream((InputStream)in);
                case "mp3" -> new Mp3Stream(in);
                case "wav" -> new WavStream(in);
                default -> throw new IOException("\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u044b\u0439 \u0444\u043e\u0440\u043c\u0430\u0442: " + String.valueOf(file.getFileName()));
            };
        }
        catch (IOException | RuntimeException e) {
            ((InputStream)in).close();
            throw e;
        }
    }

    static final class Mp3Stream
    implements FloatSampleSource {
        private final InputStream in;
        private final Bitstream bitstream;
        private final Decoder decoder = new Decoder();
        private final AudioFormat format;
        private short[] pending;
        private int pendingLen;
        private SampleBuffer lastFrame;

        Mp3Stream(InputStream in) throws IOException {
            this.in = in;
            this.bitstream = new Bitstream(in);
            if (!this.decodeFrame()) {
                throw new IOException("\u041f\u0443\u0441\u0442\u043e\u0439 \u0438\u043b\u0438 \u0431\u0438\u0442\u044b\u0439 MP3");
            }
            this.format = new AudioFormat(this.lastFrame.getSampleFrequency(), 16, this.lastFrame.getChannelCount(), true, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        private boolean decodeFrame() throws IOException {
            try {
                while (true) {
                    Header h;
                    if ((h = this.bitstream.readFrame()) == null) {
                        return false;
                    }
                    try {
                        SampleBuffer out;
                        this.lastFrame = out = (SampleBuffer)this.decoder.decodeFrame(h, this.bitstream);
                        this.pending = out.getBuffer();
                        this.pendingLen = out.getBufferLength();
                        boolean bl = true;
                        return bl;
                    }
                    catch (Exception exception) {}
                    continue;
                    finally {
                        this.bitstream.closeFrame();
                        continue;
                    }
                    break;
                }
            }
            catch (Exception e) {
                throw new IOException("\u041e\u0448\u0438\u0431\u043a\u0430 \u0447\u0442\u0435\u043d\u0438\u044f MP3", e);
            }
        }

        public AudioFormat method_19719() {
            return this.format;
        }

        public boolean method_59758(FloatConsumer consumer) throws IOException {
            if (this.pending == null && !this.decodeFrame()) {
                return false;
            }
            for (int i = 0; i < this.pendingLen; ++i) {
                consumer.accept((float)this.pending[i] / 32768.0f);
            }
            this.pending = null;
            return true;
        }

        public void close() throws IOException {
            try {
                this.bitstream.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.in.close();
        }
    }

    static final class WavStream
    implements FloatSampleSource {
        private final AudioInputStream stream;
        private final AudioFormat format;
        private final byte[] buf = new byte[8192];

        WavStream(InputStream in) throws IOException {
            try {
                AudioInputStream src = AudioSystem.getAudioInputStream(in);
                AudioFormat f = src.getFormat();
                AudioFormat target = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, f.getSampleRate(), 16, Math.min(2, f.getChannels()), Math.min(2, f.getChannels()) * 2, f.getSampleRate(), false);
                this.stream = AudioSystem.getAudioInputStream(target, src);
                this.format = new AudioFormat(target.getSampleRate(), 16, target.getChannels(), true, false);
            }
            catch (UnsupportedAudioFileException e) {
                throw new IOException("\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u044b\u0439 WAV", e);
            }
            catch (IllegalArgumentException e) {
                throw new IOException("\u042d\u0442\u043e\u0442 WAV \u043d\u0435\u043b\u044c\u0437\u044f \u043f\u0435\u0440\u0435\u0432\u0435\u0441\u0442\u0438 \u0432 16 \u0431\u0438\u0442", e);
            }
        }

        public AudioFormat method_19719() {
            return this.format;
        }

        public boolean method_59758(FloatConsumer consumer) throws IOException {
            int n = this.stream.read(this.buf, 0, this.buf.length & 0xFFFFFFFE);
            if (n <= 0) {
                return false;
            }
            int i = 0;
            while (i + 1 < n) {
                short s = (short)(this.buf[i] & 0xFF | this.buf[i + 1] << 8);
                consumer.accept((float)s / 32768.0f);
                i += 2;
            }
            return true;
        }

        public void close() throws IOException {
            this.stream.close();
        }
    }
}

