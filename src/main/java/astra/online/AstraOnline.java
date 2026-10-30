/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.client.Minecraft
 */
package astra.online;

import astra.AstraClient;
import astra.builder.CanvasBoard;
import astra.online.RemoteCosmetics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;

public final class AstraOnline {
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
    private static final ConcurrentLinkedQueue<JsonObject> INBOX = new ConcurrentLinkedQueue();
    private static volatile WebSocket socket;
    private static volatile boolean connected;
    private static volatile String status;
    private static volatile boolean lost;
    private static CompletableFuture<WebSocket> sendChain;
    private static String target;
    private static UUID helloUuid;
    private static long nextConnect;
    private static int backoff;
    private static String lastProfile;
    private static int profileTicks;
    private static int friends;

    private AstraOnline() {
    }

    public static String status() {
        return status;
    }

    public static boolean connected() {
        return connected;
    }

    public static int friendsOnline() {
        return friends;
    }

    static URI normalize(String address) {
        Object a = address.trim();
        if (((String)a).isEmpty()) {
            return null;
        }
        if (((String)a).startsWith("https://")) {
            a = "wss://" + ((String)a).substring(8);
        } else if (((String)a).startsWith("http://")) {
            a = "ws://" + ((String)a).substring(7);
        } else if (!((String)a).startsWith("ws://") && !((String)a).startsWith("wss://")) {
            String host = ((String)a).split("[/:]")[0];
            boolean plain = ((String)a).matches("^[^/]*:\\d+.*") || host.matches("[0-9.]+") || host.equals("localhost");
            a = (plain ? "ws://" : "wss://") + (String)a;
        }
        URI u = URI.create((String)a);
        if (u.getPath() == null || u.getPath().isEmpty() || u.getPath().equals("/")) {
            u = u.resolve("/ws");
        }
        return u;
    }

    public static void tick(String address, String room, boolean sendMine) {
        JsonObject msg;
        Minecraft mc = Minecraft.getInstance();
        if (lost) {
            lost = false;
            RemoteCosmetics.clear();
            friends = 0;
        }
        if (mc.player == null || address.isBlank()) {
            if (socket != null) {
                AstraOnline.disconnect();
            }
            status = "\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0438\u0442\u0441\u044f, \u043a\u043e\u0433\u0434\u0430 \u0437\u0430\u0439\u0434\u0451\u0442\u0435 \u0432 \u043c\u0438\u0440";
            return;
        }
        String want = address.trim() + "|" + room.trim();
        if (!(socket == null || want.equals(target) && mc.player.getUUID().equals(helloUuid))) {
            AstraOnline.disconnect();
        }
        if (socket == null && System.currentTimeMillis() >= nextConnect) {
            AstraOnline.connect(address, room, want);
        }
        while ((msg = INBOX.poll()) != null) {
            AstraOnline.handle(msg);
        }
        if (connected && sendMine && ++profileTicks >= 20) {
            profileTicks = 0;
            String p = RemoteCosmetics.localProfile().toString();
            if (!p.equals(lastProfile)) {
                lastProfile = p;
                AstraOnline.send("{\"t\":\"profile\",\"profile\":" + p + "}");
            }
        }
    }

    private static void connect(String address, String room, String want) {
        URI uri;
        Minecraft mc = Minecraft.getInstance();
        try {
            uri = AstraOnline.normalize(address);
        }
        catch (IllegalArgumentException e) {
            status = "\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0430\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430";
            nextConnect = System.currentTimeMillis() + 10000L;
            return;
        }
        target = want;
        helloUuid = mc.player.getUUID();
        String name = mc.player.getGameProfile().name();
        status = "\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435\u2026";
        nextConnect = Long.MAX_VALUE;
        JsonObject hello = new JsonObject();
        hello.addProperty("t", "hello");
        hello.addProperty("v", (Number)1);
        hello.addProperty("uuid", helloUuid.toString());
        hello.addProperty("name", name);
        hello.addProperty("room", room.isBlank() ? "public" : room.trim());
        hello.add("profile", (JsonElement)RemoteCosmetics.localProfile());
        lastProfile = hello.get("profile").toString();
        HTTP.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(8L)).buildAsync(uri, new Listener()).whenComplete((ws, err) -> {
            if (err != null) {
                AstraOnline.failed("\u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
                return;
            }
            socket = ws;
            sendChain = CompletableFuture.completedFuture(ws);
            AstraOnline.send(hello.toString());
        });
    }

    private static void failed(String why) {
        connected = false;
        socket = null;
        lost = true;
        status = why + " \u2014 \u043f\u043e\u0432\u0442\u043e\u0440 \u0447\u0435\u0440\u0435\u0437 " + backoff + " \u0441";
        nextConnect = System.currentTimeMillis() + (long)backoff * 1000L;
        backoff = Math.min(60, backoff * 2);
    }

    public static void disconnect() {
        WebSocket ws = socket;
        socket = null;
        connected = false;
        lost = true;
        status = "\u041e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u043e";
        nextConnect = 0L;
        if (ws != null) {
            try {
                ws.sendClose(1000, "bye");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static void reconnect() {
        AstraOnline.disconnect();
        backoff = 3;
        nextConnect = 0L;
    }

    private static synchronized void send(String text) {
        if (socket == null) {
            return;
        }
        sendChain = ((CompletableFuture)sendChain.thenCompose(ws -> ws == null ? CompletableFuture.completedFuture(null) : ws.sendText(text, true))).exceptionally(e -> null);
    }

    public static void sendEmote(String emote) {
        if (!connected) {
            return;
        }
        JsonObject o = new JsonObject();
        o.addProperty("t", "emote");
        o.addProperty("emote", emote);
        AstraOnline.send(o.toString());
    }

    public static String canvasConnection() {
        return target;
    }

    public static void sendCanvas(JsonObject msg) {
        if (connected) {
            AstraOnline.send(msg.toString());
        }
    }

    private static void handle(JsonObject msg) {
        String t;
        switch (t = msg.has("t") ? msg.get("t").getAsString() : "") {
            case "canvas_snapshot": 
            case "canvas_put": 
            case "canvas_delete": 
            case "canvas_error": {
                CanvasBoard.handle(msg);
                break;
            }
            case "welcome": {
                connected = true;
                CanvasBoard.onlineReset();
                backoff = 3;
                RemoteCosmetics.clear();
                for (JsonElement el : msg.getAsJsonArray("players")) {
                    JsonObject p = el.getAsJsonObject();
                    RemoteCosmetics.apply(p.get("uuid").getAsString(), p.get("name").getAsString(), p.getAsJsonObject("profile"));
                }
                friends = RemoteCosmetics.count();
                status = "\u041e\u043d\u043b\u0430\u0439\u043d \u00b7 \u0432 \u043a\u043e\u043c\u043d\u0430\u0442\u0435: " + friends;
                AstraClient.LOGGER.info("Astra Online: \u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u043e, \u0432 \u043a\u043e\u043c\u043d\u0430\u0442\u0435 {}", (Object)friends);
                break;
            }
            case "profile": {
                RemoteCosmetics.apply(msg.get("uuid").getAsString(), msg.get("name").getAsString(), msg.getAsJsonObject("profile"));
                friends = RemoteCosmetics.count();
                status = "\u041e\u043d\u043b\u0430\u0439\u043d \u00b7 \u0432 \u043a\u043e\u043c\u043d\u0430\u0442\u0435: " + friends;
                break;
            }
            case "leave": {
                RemoteCosmetics.remove(msg.get("uuid").getAsString());
                friends = RemoteCosmetics.count();
                status = "\u041e\u043d\u043b\u0430\u0439\u043d \u00b7 \u0432 \u043a\u043e\u043c\u043d\u0430\u0442\u0435: " + friends;
                break;
            }
            case "emote": {
                RemoteCosmetics.emote(msg.get("uuid").getAsString(), msg.get("emote").getAsString());
                break;
            }
            case "error": {
                status = "\u0421\u0435\u0440\u0432\u0435\u0440: " + msg.get("msg").getAsString();
                break;
            }
        }
    }

    static {
        status = "\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u043e";
        sendChain = CompletableFuture.completedFuture(null);
        target = "";
        backoff = 3;
        lastProfile = "";
    }

    private static final class Listener
    implements WebSocket.Listener {
        private final StringBuilder buffer = new StringBuilder();

        private Listener() {
        }

        @Override
        public void onOpen(WebSocket ws) {
            ws.request(1L);
        }

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            if (this.buffer.length() + data.length() > 16000000) {
                this.buffer.setLength(0);
                ws.abort();
                return null;
            }
            this.buffer.append(data);
            if (last) {
                try {
                    if (INBOX.size() < 8) {
                        INBOX.add(JsonParser.parseString((String)this.buffer.toString()).getAsJsonObject());
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.buffer.setLength(0);
            }
            ws.request(1L);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int code, String reason) {
            if (socket == ws) {
                AstraOnline.failed("\u0421\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u0437\u0430\u043a\u0440\u044b\u0442\u043e");
            }
            return null;
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            if (socket == ws || socket == null) {
                AstraOnline.failed("\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0435\u0442\u0438");
            }
        }
    }
}

