package astra.utils;

import astra.AstraClient;
import astra.gui.screens.ClickGuiScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.TitleScreen;

/** Opt-in CI boot check; never enabled for normal players. */
public final class ClientSmokeTest {
    private static int ticks;
    private static boolean opened;
    private ClientSmokeTest() {}

    public static void init() {
        if (!Boolean.getBoolean("astra.smokeTest")) return;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!opened) {
                if (!(client.gui.screen() instanceof TitleScreen)) return;
                if (++ticks < 40) return;
                client.gui.setScreen(new ClickGuiScreen(null));
                opened = true;
                ticks = 0;
            } else if (++ticks >= 60) {
                AstraClient.LOGGER.info("ASTRA_SMOKE_TEST_PASS: title and ClickGUI rendered");
                client.stop();
            }
        });
    }
}
