package astra.utils;

import astra.AstraClient;
import astra.gui.screens.ClickGuiScreen;
import astra.config.ConfigManager;
import astra.decor.DecorManager;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.ProfileStyle;
import astra.module.modules.cosmetic.SoundsModule;
import astra.render.ArtManager;
import astra.settings.Setting;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.TitleScreen;

/** Opt-in CI boot check; never enabled for normal players. */
public final class ClientSmokeTest {
    private static int ticks;
    private static boolean opened;
    private static JsonObject original;
    private ClientSmokeTest() {}

    public static void init() {
        if (!Boolean.getBoolean("astra.smokeTest")) return;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!opened) {
                if (!client.isGameLoadFinished() || client.gui.overlay() != null) return;
                if (!(client.gui.screen() instanceof TitleScreen)) {
                    client.gui.setScreen(new TitleScreen());
                    ticks = 0;
                    return;
                }
                if (++ticks < 40) return;
                verifyPersonalization();
                ClickGuiScreen screen = new ClickGuiScreen(null);
                screen.setCategory(Category.COSMETICS);
                client.gui.setScreen(screen);
                opened = true;
                ticks = 0;
            } else if (++ticks == 30) {
                net.minecraft.client.Screenshot.grab(client.gameDirectory, "astra-smoke-profile.png",
                        client.gameRenderer.mainRenderTarget(), 1, message -> AstraClient.LOGGER.info("Smoke screenshot: {}", message.getString()));
            } else if (ticks >= 60) {
                AstraClient.LOGGER.info("ASTRA_SMOKE_TEST_PASS: title and ClickGUI rendered");
                ConfigManager.applyModules(original, true, false);
                client.stop();
            }
        });
    }

    private static void verifyMixinTargets() throws ClassNotFoundException {
        String[] targets = {
            "net.minecraft.client.Camera",
            "net.minecraft.client.ClientClockManager",
            "net.minecraft.client.Minecraft",
            "net.minecraft.client.MouseHandler",
            "net.minecraft.client.gui.Hud",
            "net.minecraft.client.gui.components.AbstractButton",
            "net.minecraft.client.gui.components.AbstractSliderButton",
            "net.minecraft.client.gui.components.AbstractWidget",
            "net.minecraft.client.gui.components.ChatComponent",
            "net.minecraft.client.gui.components.CommandSuggestions",
            "net.minecraft.client.gui.components.CommandSuggestions$SuggestionsList",
            "net.minecraft.client.gui.components.LogoRenderer",
            "net.minecraft.client.gui.components.PlainTextButton",
            "net.minecraft.client.gui.components.PlayerTabOverlay",
            "net.minecraft.client.gui.components.SplashRenderer",
            "net.minecraft.client.gui.render.GuiRenderer",
            "net.minecraft.client.gui.screens.ChatScreen",
            "net.minecraft.client.gui.screens.Screen",
            "net.minecraft.client.gui.screens.TitleScreen",
            "net.minecraft.client.multiplayer.ClientPacketListener",
            "net.minecraft.client.particle.ParticleEngine",
            "net.minecraft.client.player.AbstractClientPlayer",
            "net.minecraft.client.renderer.GameRenderer",
            "net.minecraft.client.renderer.ItemInHandRenderer",
            "net.minecraft.client.renderer.ScreenEffectRenderer",
            "net.minecraft.client.renderer.item.ItemModelResolver",
            "net.minecraft.client.renderer.item.ItemStackRenderState",
            "net.minecraft.client.renderer.special.ShieldSpecialRenderer",
            "net.minecraft.client.renderer.state.gui.GuiRenderState",
            "net.minecraft.client.sounds.MusicManager",
            "net.minecraft.client.sounds.SoundBufferLibrary",
            "net.minecraft.world.entity.LivingEntity",
            "net.minecraft.world.level.Level",
            "net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess",
            "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess"
        };
        for (String target : targets) Class.forName(target, false, ClientSmokeTest.class.getClassLoader());
        AstraClient.LOGGER.info("ASTRA_MIXIN_TEST_PASS: all {} target classes transformed", targets.length);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void set(Module module, String name, Object value) {
        Setting setting = module.getSettings().stream().filter(s -> s.getName().equals(name)).findFirst().orElseThrow();
        setting.set(value);
    }

    private static void verifyPersonalization() {
        try {
            verifyMixinTargets();
            original = ConfigManager.serializeModules(true);
            ProfileStyle profile = ProfileStyle.get();
            SoundsModule sounds = ModuleManager.get(SoundsModule.class);
            Path avatar = ArtManager.artDir().resolve("smoke-avatar.png");
            Files.createDirectories(avatar.getParent());
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(32, 48, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            for (int y=0; y<48; y++) for (int x=0; x<32; x++) image.setRGB(x, y, 0xffe070b0);
            javax.imageio.ImageIO.write(image, "png", avatar.toFile());
            if (!ProfileStyle.avatarOptions().contains("Файл: smoke-avatar.png")) throw new IllegalStateException("Avatar scan failed");
            if (DecorManager.texture("smoke-avatar.png") == null) throw new IllegalStateException("Avatar upload failed");
            set(profile, "Аватар", "Файл: smoke-avatar.png");
            set(profile, "Тег", "Свой");
            set(profile, "Свой тег", "  CI Sakura  ");
            set(sounds, "Своё приветствие", true);
            set(sounds, "Заголовок приветствия", "Привет, игрок!");
            set(sounds, "Подпись приветствия", "Проверка 26.2");
            JsonObject saved = ConfigManager.serializeModules(true);
            Path file = ConfigManager.root().resolve("smoke-profile.json");
            ConfigManager.writeJson(file, saved);
            set(profile, "Тег", "Astra");
            set(sounds, "Своё приветствие", false);
            ConfigManager.applyModules(ConfigManager.readJson(file), true, false);
            Files.deleteIfExists(file);
            if (!profile.tag().equals("CI Sakura")) throw new IllegalStateException("Custom tag did not survive persistence");
            String[] caption = sounds.welcomeCaption("join_1");
            if (!caption[0].equals("Привет, игрок!") || !caption[1].equals("Проверка 26.2")) throw new IllegalStateException("Welcome did not survive persistence");
            AstraClient.LOGGER.info("ASTRA_PERSONALIZATION_TEST_PASS: avatar upload, tag and welcome disk round trip");
        } catch (Exception e) {
            throw new IllegalStateException("Personalization smoke test failed", e);
        }
    }
}
