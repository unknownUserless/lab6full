package client;

import history.PeopleMap;

import java.io.Serializable;
import java.net.SocketAddress;

public class MainPacket implements Serializable {

    public final SocketAddress address;
    public final PeopleMap map;
    public final int id;

    public MainPacket(PeopleMap map, int id, SocketAddress address) {
        this.map = map;
        this.id = id;
        this.address = address;
    }
}
