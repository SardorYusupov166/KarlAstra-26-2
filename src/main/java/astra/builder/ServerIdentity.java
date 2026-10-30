/*
 * Decompiled with CFR 0.152.
 */
package astra.builder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Locale;

public final class ServerIdentity {
    private ServerIdentity() {
    }

    public static String peer(SocketAddress address) {
        InetSocketAddress remote;
        if (!(address instanceof InetSocketAddress) || (remote = (InetSocketAddress)address).isUnresolved()) {
            return null;
        }
        Object ip = remote.getAddress().getHostAddress().toLowerCase(Locale.ROOT);
        int zone = ((String)ip).indexOf(37);
        if (zone >= 0) {
            ip = ((String)ip).substring(0, zone);
        }
        if (((String)ip).indexOf(58) >= 0) {
            ip = "[" + (String)ip + "]";
        }
        return "server:" + (String)ip + ":" + remote.getPort();
    }
}

