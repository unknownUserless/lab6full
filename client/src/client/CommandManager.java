package client;

import history.Squad;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class CommandManager {

    private boolean useUserFile;
    private Connector.Receiver receiver;
    private Connector.Sender sender;
    private FileWorker fileWorker;
    private Console console;
    private Squad squad;

    public CommandManager(Connector connector, Console console, boolean useUserFile) {
        this.useUserFile = useUserFile;
        this.receiver = connector.receiver;
        this.sender = connector.sender;
        this.fileWorker = new FileWorker(Main.getFile());
        this.console = console;
    }

    public Squad getSquad() {
        return squad;
    }


    public void start() {
        boolean cont = false;
        MyPacket lastPacket = null;
        while (!cont) {
            String[] arr = commandAndArgsArray(console.readCommand());
            String command = arr[0];
            MyPacket pack = new MyPacket(command);
            String arguments = null;
            if (arr.length == 2) {
                arguments = arr[1].replace(" ", "");
            }

            switch (command) {
                case "import":
                    String str = fileWorker.getFileContent();
                    pack.setAttachment(str);
                    useUserFile = true;
                    break;
                case "load":
                    useUserFile = false;
                    break;
                case "cont":
                    cont = true;
                    break;
                case "add":
                    if (isJsonCorrect(command, arguments) && isMembersInJson(arguments)) {
                        pack.setArguments(arguments);
                    } else {
                        System.err.println("Json usage: " + jsonUsage);
                        continue;
                    }
                case "get":
                case "remove":
                    if (isJsonCorrect(command, arguments)) {
                        pack.setArguments(arguments);
                    } else {
                        System.err.println("Json usage: " + jsonUsage);
                        continue;
                    }
                    break;
                case "r":
                    pack = lastPacket;
                    break;
                case "connect":
                    Main.connect();
                    continue;
            }

            if (cont) continue;
            sender.sendObj(pack);
            lastPacket = pack;
            switch (command) {
                case "save":
                    if (useUserFile) {
                        try {
                            String fileContent = receiver.receiveString(400);
                            fileWorker.saveFileContent(fileContent);
                        } catch (SocketTimeoutException e){
                            System.err.println("Содержимое файла не получено, повторите команду введя \"r\"");
                            continue;
                        }
                    }
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "get":
                    try {
                        Object o = receiver.receiveObj();
                        if (o instanceof Squad){
                            squad = (Squad)o;
                            squad.toBorn();
                        }
                    } catch (SocketTimeoutException e){
                        System.err.println("Получение отряда не произошло, повторите запрос введя \"r\"");
                        continue;
                    } catch (ClassNotFoundException e){
                        System.err.println("Класс не найден");
                        continue;
                    }
            }
            try {
                System.out.println(receiver.receiveString(600));
            } catch (SocketTimeoutException e) {
                System.err.println("Время ожидания превышено, чтобы повторить вашу команду введите \"r\"" +
                        "\nЧтобы переподключиться к серверу используйте команду connect");

            }

        }

    }

    private String[] commandAndArgsArray(String str) {
        return str.split(" ", 2);
    }


    private boolean isJsonCorrect(String command, String arguments) {
        boolean result = true;
        if (arguments == null) {
            new WrongArguments("null", command).info();
            return false;
        } else {
            if (!arguments.startsWith("{") || !arguments.endsWith("}")) {
                System.err.println("Проверьте json объект на наличие фигурных скобок");
                result = false;
            }
            if (!arguments.contains("\"name\"")) {
                System.err.println("Отряд должен иметь название (\"name\":\"Name of squad\")");
                result = false;
            }
            return result;
        }
    }

    private boolean isMembersInJson(String arguments){
        if (arguments.contains("\"members\"")) {
            return true;
        } else {
            System.err.println("Отряд должен состоять из людей (\"members\":[ ... ])");
            return false;
        }
    }

    private final String jsonUsage = "command {\"name\":\"Название отряда\", " +
            "\"members\":[\"имя человека1\", \"имя человека2\"]}";
    //add {"name":"Big", "members":["Стекляшкин", "Винтик", "Шпунтик"]}

}
