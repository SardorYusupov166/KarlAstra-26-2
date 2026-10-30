/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.util.Util
 */
package astra.module.modules.visual;

import astra.AstraClient;
import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.Module;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;

public class ShadersModule
extends Module {
    private static final String NO_PACKS = "\u041d\u0435\u0442 \u0448\u0435\u0439\u0434\u0435\u0440\u043f\u0430\u043a\u043e\u0432";
    private final ModeSetting pack = this.add(new ModeSetting("#\u0428\u0435\u0439\u0434\u0435\u0440\u043f\u0430\u043a", "\u041d\u0435\u0442 \u0448\u0435\u0439\u0434\u0435\u0440\u043f\u0430\u043a\u043e\u0432", new String[0]).dynamic(this::listPacks));
    private final ButtonSetting apply = this.add(new ButtonSetting("\u041f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c", "\u041f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c \u0448\u0435\u0439\u0434\u0435\u0440\u043f\u0430\u043a", this::applyPack));
    private final ButtonSetting settings = this.add(new ButtonSetting("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", "\u041f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u044b \u0448\u0435\u0439\u0434\u0435\u0440\u0430 (Iris)", this::openIrisScreen));
    private final ButtonSetting folder = this.add(new ButtonSetting("\u041f\u0430\u043f\u043a\u0430", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 shaderpacks", this::openFolder));
    private String status = "";
    private static boolean cachedInUse;
    private static long checkedAt;

    public ShadersModule() {
        super("shaders", "\u0428\u0435\u0439\u0434\u0435\u0440\u044b", "\u0422\u0435\u043d\u0438, \u043e\u0441\u0432\u0435\u0449\u0435\u043d\u0438\u0435 \u0438 \u0430\u0442\u043c\u043e\u0441\u0444\u0435\u0440\u0430 \u0447\u0435\u0440\u0435\u0437 Iris Shaders.", Category.VISUAL, Icon.SUN);
    }

    public static boolean irisLoaded() {
        return FabricLoader.getInstance().isModLoaded("iris");
    }

    private Path packsDir() {
        return FabricLoader.getInstance().getGameDir().resolve("shaderpacks");
    }

    private List<String> listPacks() {
        ArrayList<String> list = new ArrayList<String>();
        Path dir = this.packsDir();
        if (Files.isDirectory(dir, new LinkOption[0])) {
            try (Stream<Path> files = Files.list(dir);){
                files.filter(p -> Files.isDirectory(p, new LinkOption[0]) || p.getFileName().toString().toLowerCase().endsWith(".zip")).map(p -> p.getFileName().toString()).sorted().forEach(list::add);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (list.isEmpty()) {
            list.add(NO_PACKS);
        }
        return list;
    }

    private static Object irisConfigApi() throws ReflectiveOperationException {
        Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
        Object instance = api.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        return api.getMethod("getConfig", new Class[0]).invoke(instance, new Object[0]);
    }

    public static boolean shadersInUse() {
        long now = System.currentTimeMillis();
        if (now - checkedAt < 500L) {
            return cachedInUse;
        }
        checkedAt = now;
        cachedInUse = false;
        if (!ShadersModule.irisLoaded()) {
            return false;
        }
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = api.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            cachedInUse = (Boolean)api.getMethod("isShaderPackInUse", new Class[0]).invoke(instance, new Object[0]);
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            cachedInUse = false;
        }
        return cachedInUse;
    }

    private void setShadersEnabled(boolean enabled) {
        if (!ShadersModule.irisLoaded()) {
            return;
        }
        try {
            Object config = ShadersModule.irisConfigApi();
            config.getClass().getMethod("setShadersEnabledAndApply", Boolean.TYPE).invoke(config, enabled);
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            AstraClient.LOGGER.warn("Iris: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0448\u0435\u0439\u0434\u0435\u0440\u044b", (Throwable)e);
            this.status = "\u041e\u0448\u0438\u0431\u043a\u0430 Iris API";
        }
    }

    private void applyPack() {
        if (!ShadersModule.irisLoaded()) {
            this.status = "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u0435 Iris Shaders";
            return;
        }
        String name = (String)this.pack.get();
        if (NO_PACKS.equals(name)) {
            this.status = "\u041f\u043e\u043b\u043e\u0436\u0438\u0442\u0435 \u0448\u0435\u0439\u0434\u0435\u0440\u043f\u0430\u043a \u0432 \u043f\u0430\u043f\u043a\u0443 shaderpacks";
            return;
        }
        try {
            Class<?> iris = Class.forName("net.irisshaders.iris.Iris");
            Object cfg = iris.getMethod("getIrisConfig", new Class[0]).invoke(null, new Object[0]);
            cfg.getClass().getMethod("setShaderPackName", String.class).invoke(cfg, name);
            cfg.getClass().getMethod("setShadersEnabled", Boolean.TYPE).invoke(cfg, true);
            cfg.getClass().getMethod("save", new Class[0]).invoke(cfg, new Object[0]);
            Method reload = iris.getMethod("reload", new Class[0]);
            reload.invoke(null, new Object[0]);
            this.status = "\u041f\u0440\u0438\u043c\u0435\u043d\u0451\u043d: " + name;
            if (!this.isEnabled()) {
                this.setEnabled(true);
            }
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            AstraClient.LOGGER.warn("Iris: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c \u0448\u0435\u0439\u0434\u0435\u0440\u043f\u0430\u043a", (Throwable)e);
            this.status = "\u042d\u0442\u0430 \u0432\u0435\u0440\u0441\u0438\u044f Iris \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442 \u0432\u044b\u0431\u043e\u0440 \u2014 \u043e\u0442\u043a\u0440\u043e\u0439\u0442\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 Iris";
        }
    }

    private void openIrisScreen() {
        if (!ShadersModule.irisLoaded()) {
            this.status = "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u0435 Iris Shaders";
            return;
        }
        try {
            Class<?> screen = Class.forName("net.irisshaders.iris.gui.screen.ShaderPackScreen");
            Object s = screen.getConstructor(Screen.class).newInstance(ShadersModule.mc.field_1755);
            mc.method_1507((Screen)s);
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            AstraClient.LOGGER.warn("Iris: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u044d\u043a\u0440\u0430\u043d \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a", (Throwable)e);
            this.status = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u044d\u043a\u0440\u0430\u043d Iris";
        }
    }

    private void openFolder() {
        try {
            Files.createDirectories(this.packsDir(), new FileAttribute[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        Util.method_668().method_672(this.packsDir().toFile());
    }

    @Override
    protected void onEnable() {
        this.setShadersEnabled(true);
    }

    @Override
    protected void onDisable() {
        this.setShadersEnabled(false);
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 26.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, ColorUtil.withAlpha(ui.theme.bg(), 0.55f));
                boolean iris = ShadersModule.irisLoaded();
                Render2D.circle(ui.ctx, x + 8.0f, y + 8.0f, 2.5f, iris ? -11870592 : -770210);
                Fonts.draw(ui.ctx, iris ? (ShadersModule.shadersInUse() ? "Iris: \u0448\u0435\u0439\u0434\u0435\u0440\u044b \u0430\u043a\u0442\u0438\u0432\u043d\u044b" : "Iris \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d") : "Iris Shaders \u043d\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d", x + 15.0f, y + 4.5f, 6.8f, ui.theme.text(), true);
                String line = !ShadersModule.this.status.isEmpty() ? ShadersModule.this.status : (iris ? "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u043f\u0430\u043a \u0438 \u043d\u0430\u0436\u043c\u0438\u0442\u0435 \u00ab\u041f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c\u00bb" : "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u0435 Iris \u0434\u043b\u044f \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0438 \u0448\u0435\u0439\u0434\u0435\u0440\u043e\u0432");
                Fonts.draw(ui.ctx, Fonts.trim(line, w - 12.0f, 6.0f, false), x + 6.0f, y + 15.0f, 6.0f, ui.theme.textMuted(), false);
            }
        };
    }
}

