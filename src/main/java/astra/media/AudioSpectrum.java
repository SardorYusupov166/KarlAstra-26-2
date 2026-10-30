/*
 * Decompiled with CFR 0.152.
 */
package astra.media;

import astra.AstraClient;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public final class AudioSpectrum {
    public static final int BANDS = 32;
    private static final int N = 1024;
    private static final float[] LEVELS = new float[32];
    private static volatile boolean running;
    private static volatile boolean available;
    private static volatile boolean tried;
    private static Thread thread;
    private static String device;

    private AudioSpectrum() {
    }

    public static boolean available() {
        return available;
    }

    public static String device() {
        return device;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static float[] levels() {
        float[] fArray = LEVELS;
        synchronized (LEVELS) {
            // ** MonitorExit[var0] (shouldn't be in output)
            return (float[])LEVELS.clone();
        }
    }

    public static synchronized void start() {
        if (running || tried) {
            return;
        }
        tried = true;
        Mixer.Info found = null;
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            String n = (info.getName() + " " + info.getDescription()).toLowerCase(Locale.ROOT);
            if (!n.contains("stereo mix") && !n.contains("\u0441\u0442\u0435\u0440\u0435\u043e \u043c\u0438\u043a\u0448\u0435\u0440") && !n.contains("what u hear") && !n.contains("loopback")) continue;
            found = info;
            break;
        }
        if (found == null) {
            AstraClient.LOGGER.info("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440: \u00ab\u0421\u0442\u0435\u0440\u0435\u043e \u043c\u0438\u043a\u0448\u0435\u0440\u00bb \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u2014 \u0431\u0443\u0434\u0435\u0442 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f");
            return;
        }
        Mixer.Info info = found;
        running = true;
        thread = new Thread(() -> AudioSpectrum.capture(info), "Astra spectrum");
        thread.setDaemon(true);
        thread.start();
    }

    public static synchronized void stop() {
        running = false;
        tried = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private static void capture(Mixer.Info info) {
        AudioFormat fmt = new AudioFormat(44100.0f, 16, 2, true, false);
        try (TargetDataLine line = (TargetDataLine)AudioSystem.getMixer(info).getLine(new DataLine.Info(TargetDataLine.class, fmt));){
            line.open(fmt, 8192);
            line.start();
            device = info.getName();
            available = true;
            AstraClient.LOGGER.info("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440: \u0441\u043b\u0443\u0448\u0430\u044e \u00ab{}\u00bb", (Object)info.getName());
            byte[] buf = new byte[4096];
            float[] mono = new float[1024];
            float[] bands = new float[32];
            while (running) {
                int r;
                for (int read = 0; read < buf.length && running && (r = line.read(buf, read, buf.length - read)) > 0; read += r) {
                }
                for (int i = 0; i < 1024; ++i) {
                    short l = (short)(buf[i * 4] & 0xFF | buf[i * 4 + 1] << 8);
                    short rr = (short)(buf[i * 4 + 2] & 0xFF | buf[i * 4 + 3] << 8);
                    float w = 0.5f - 0.5f * (float)Math.cos(Math.PI * 2 * (double)i / 1023.0);
                    mono[i] = (float)(l + rr) / 65536.0f * w;
                }
                AudioSpectrum.spectrum(mono, bands);
                float[] fArray = LEVELS;
                // MONITORENTER : LEVELS
                for (int b = 0; b < 32; ++b) {
                    AudioSpectrum.LEVELS[b] = bands[b] > LEVELS[b] ? bands[b] : LEVELS[b] * 0.85f + bands[b] * 0.15f;
                }
                // MONITOREXIT : fArray
            }
            return;
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043b\u0443\u0448\u0430\u0442\u044c \u00ab{}\u00bb: {}", (Object)info.getName(), (Object)e.getMessage());
            return;
        }
        finally {
            available = false;
            running = false;
        }
    }

    private static void spectrum(float[] x, float[] out) {
        double fMin = 40.0;
        double fMax = 16000.0;
        for (int b = 0; b < 32; ++b) {
            double f0 = fMin * Math.pow(fMax / fMin, (double)b / 32.0);
            double f1 = fMin * Math.pow(fMax / fMin, (double)(b + 1) / 32.0);
            int k0 = Math.max(1, (int)(f0 * 1024.0 / 44100.0));
            int k1 = Math.max(k0 + 1, (int)(f1 * 1024.0 / 44100.0));
            double sum = 0.0;
            int count = 0;
            for (int k = k0; k < k1 && k < 512; k += Math.max(1, (k1 - k0) / 4)) {
                double re = 0.0;
                double im = 0.0;
                double w = Math.PI * 2 * (double)k / 1024.0;
                for (int n = 0; n < 1024; n += 2) {
                    re += (double)x[n] * Math.cos(w * (double)n);
                    im -= (double)x[n] * Math.sin(w * (double)n);
                }
                sum += Math.sqrt(re * re + im * im);
                ++count;
            }
            double mag = count > 0 ? sum / (double)count : 0.0;
            double db = 20.0 * Math.log10(mag + 1.0E-6);
            out[b] = (float)Math.max(0.0, Math.min(1.0, (db + 38.0) / 46.0));
        }
    }

    static {
        device = "";
    }
}

