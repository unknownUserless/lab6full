package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Responder extends Thread {
    private Command command;

    public Responder(Command command) {
        this.command = command;
    }

    @Override
    public void run() {
        try {
            String result = command.execute();
            command.getConnector().sender.sendString(result);
            Main.logger.info("Ответ отправлен");
            Main.logger.fine("Отправлено: " + result);
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
