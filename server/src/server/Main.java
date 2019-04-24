package server;

import client.MainPacket;
import client.MyPacket;
import client.ServerRespondPacket;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {

    private static SocketAddress serverAddress;
    public static final Logger logger = Logger.getLogger("server");
    public static final File file = new File("server.csv");
    private static Users users = new Users();
    private static Selector selector;
    private static HashMap<Integer, SelectionKey> currentConnections = new HashMap<>();
    private static Connector mainConnector;

    public static void main(String[] args) throws Exception {

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.OFF);

        int mainPort = -1;
        if (args.length < 1) {
            System.out.println("Неверное число аргументов, 1 - порт,  2 - уровень логгера");
            System.exit(2);
        } else if (args.length < 3) {
            try {
                mainPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Первый аргумент должен быть числом [1500; 40000]");
                System.exit(2);
            }
            if (args.length == 2) {
                handler.setLevel(Level.parse(args[1].toUpperCase()));
            }
        }

        logger.addHandler(handler);
        System.out.println("Main port: " + mainPort);
        System.out.println("Logger level: " + logger.getHandlers()[0].getLevel());


        if (!file.exists()) {
            System.err.println("Файл с коллекцией не существует,\nпожалуйста создайте файл и запустите сервер");
            System.exit(1);
        }

        try {
            users.load();
        } catch (Throwable e) {
            users.clearFile();
            users.load();
        }

        selector = Selector.open();

        DatagramChannel mainChannel = DatagramChannel.open();

        serverAddress = new InetSocketAddress(InetAddress.getLocalHost(), mainPort);

        mainChannel.bind(serverAddress);

        mainChannel.configureBlocking(false);
        mainChannel.register(selector, SelectionKey.OP_READ);


        while (true) {
            if (selector.select() == 0) continue;
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                try {
                    if (key.attachment() == null) {
                        handleMainKey(key);
                    } else {
                        handleUserKey(key);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static void handleMainKey(SelectionKey key) throws IOException, ClassNotFoundException {
        if (mainConnector == null) {
            mainConnector = new Connector(key);
        }
        MainPacket receivedPacket = (MainPacket) mainConnector.receiver.receiveObj();
        mainConnector.setSendAddress(receivedPacket.address);

        User user;

        logger.info("В данный момент подключены: " + currentConnections.keySet().stream().map(String::valueOf).
                collect(Collectors.joining(", ")));
        logger.info("Запрос на соединение от пользователя " + receivedPacket.id);

        if (currentConnections.containsKey(receivedPacket.id)) {
            SelectionKey removedKey = currentConnections.remove(receivedPacket.id);
            removedKey.channel().close();
            removedKey.cancel();
        }

        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(null);

        boolean useUserFile;
        if (users.getUsers().containsKey(receivedPacket.id)) {
            user = users.getUsers().get(receivedPacket.id);
            user.setSendAddress(receivedPacket.address);
            user.getLogger().setLocalAddress(channel.getLocalAddress());
            useUserFile = user.getPatternSquads().isUseUserFile();
            System.out.println("Пользователь " + receivedPacket.id + " снова подключился");
        } else {
            user = new User(receivedPacket.map, receivedPacket.id, receivedPacket.address);
            user.getLogger().setLocalAddress(channel.getLocalAddress());
            useUserFile = false;
            users.addUser(user);
            System.out.println("Добавлен новый пользователь " + receivedPacket.id);
            logger.finest(String.valueOf(receivedPacket.map.size()));
        }
        mainConnector.sender.sendObj(new ServerRespondPacket(channel.getLocalAddress(), useUserFile));
        currentConnections.put(user.getId(), channel.register(selector, SelectionKey.OP_READ, user));
    }


    private static void handleUserKey(SelectionKey key) throws IOException, ClassNotFoundException {
        Connector connector = new Connector(key);
        MyPacket packet = (MyPacket) connector.receiver.receiveObj();
        logger.fine("command = " + packet.getCommand());
        logger.fine("arguments = " + packet.getArguments());
        logger.fine("attachment = " + packet.getAttachment());
        Command command = new Command((User) key.attachment(), packet, connector);
        if (command.isExit()) {
            key.channel().close();
            key.cancel();
            int id = ((User) key.attachment()).getId();
            System.out.println("Пользователь " + id + " вышел");
        } else {
            new Thread(new Responder(command)).start();
        }
    }
}