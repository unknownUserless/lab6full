package server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UserLogger implements Serializable {
    private int id;

    transient private int remotePort;
    transient private int localPort;

    public UserLogger(int id, SocketAddress remoteAddress){
        this.id = id;
        setRemoteAddress(remoteAddress);
    }

    public void info(String str){
        System.out.println("User "+id+"("+ remotePort + "->" + localPort + "): " + str);
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        remotePort = ((InetSocketAddress)remoteAddress).getPort();
    }

    public void setLocalAddress(SocketAddress localAddress) {
        localPort = ((InetSocketAddress)localAddress).getPort();
    }
}
