package server;

import history.PeopleMap;

import java.io.Serializable;
import java.net.SocketAddress;

public class User implements Serializable {
    private int id;
    private UserLogger logger;
    private PatternSquads patternSquads;
    private SocketAddress sendAddress;

    public User(PeopleMap map, int id, SocketAddress address){
        this.id = id;
        this.sendAddress = address;
        if (map.size() == 0){
            System.err.println("Коллекция людей пользователя " + id + " пустая");
        }
        this.logger = new UserLogger(id, sendAddress);
        try {
            patternSquads = new PatternSquads(map);
        } catch (NoPersonsException e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public UserLogger getLogger() {
        return logger;
    }

    public int getId() {
        return id;
    }

    public SocketAddress getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(SocketAddress sendAddress) {
        this.sendAddress = sendAddress;
        this.logger.setRemoteAddress(sendAddress);
    }

    public PatternSquads getPatternSquads(){
        return patternSquads;
    }
}
