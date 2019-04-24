package client;

import java.io.Serializable;
import java.net.SocketAddress;

public class ServerRespondPacket implements Serializable {

    public final SocketAddress address;
    public final boolean useUserFile;

    public ServerRespondPacket(SocketAddress address, boolean useUserFile) {
        this.address = address;
        this.useUserFile = useUserFile;
    }
}