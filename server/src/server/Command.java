package server;

import client.Empty;
import history.Human;
import client.MyPacket;
import history.Location;
import history.Squad;

import java.io.IOException;
import java.util.*;

public class Command {
    private final String command;
    private final String arguments;
    private final User user;
    private MyPacket packet;
    private Connector connector;

    public Connector getConnector() {
        return connector;
    }

    public User getUser() {
        return user;
    }

    private boolean exit = false;

    public boolean isExit() {
        return exit;
    }

    public Command(User user, MyPacket packet, Connector connector) {
        this.user = user;
        this.packet = packet;
        this.command = packet.getCommand();
        this.connector = connector;

        if (command.replace(" ", "").equals("exit")) {
            exit = true;
        }
        this.arguments = packet.getArguments();

        user.getLogger().info("Команда " + this.command);
        if (arguments != null) user.getLogger().info("Аргументы " + this.arguments);

    }

    public String execute() throws IOException {
        switch (command) {
            case "show":
                return show();
            case "info":
                return info();
            case "load":
                return load();
            case "remove_first":
                return remove_first();
            case "remove_last":
                return remove_last();
            case "save":
                return save();
            case "import":
                return importing();
            case "add":
                return add();
            case "remove":
                return remove();
            case "get":
                return get();
            case "help":
                return help();
            case "sort":
                return sort();
            default:
                return "Команда " + command + " не найдена";
        }
    }


    private String show() {
        class Table {
            private ArrayList<String> names = new ArrayList<>();
            private ArrayList<String> squadNames = new ArrayList<>();

            private String format;
            private int lineLength;

            private Table() {
                if (user.getPatternSquads().getSquads().size() == 0){

                }
                int maxNamesLength = 0;
                int maxSquadNameLength = 0;
                for (Squad squad : user.getPatternSquads().getSquads()) {

                    StringJoiner names = new StringJoiner(", ");
                    int namesLength = 0;
                    for (Human cos : squad.getCosmonauts()) {
                        namesLength += cos.getName().length() + 1;
                        names.add(cos.getName());
                    }
                    this.names.add(names.toString());

                    String squadName = squad.getName();
                    squadNames.add(squadName);

                    if (namesLength > maxNamesLength) maxNamesLength = namesLength;
                    if (squadName.length() > maxSquadNameLength) maxSquadNameLength = squadName.length();
                }
                lineLength = maxNamesLength + maxSquadNameLength + 20;
                format = "| %5s | %" + (maxSquadNameLength) + "s | %" + (maxNamesLength + 5) + "s |";
            }


            private String setLine(char ch, int length) {
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < length; i++) str.append(ch);
                return str.toString();
            }

            private String print() {
                StringJoiner out = new StringJoiner("\n");
                out.add(setLine('-', lineLength));
                out.add(String.format(format, "index", "name", "members"));
                out.add(setLine('=', lineLength));
                for (int i = 0; i < user.getPatternSquads().getSquads().size(); i++) {
                    out.add(String.format(format, Integer.toString(i), squadNames.get(i), names.get(i)));
                }
                out.add(setLine('-', lineLength));
                return out.toString();
            }

        }
        if (user.getPatternSquads().getSquads().size() != 0) {
            Table table = new Table();
            return table.print();
        } else {
            Main.logger.fine("Squads size == 0");
            return "В коллекции нет отрядов, добавьте их, используя команду add";
        }
    }

    private String info() {
        Collection collection = user.getPatternSquads().getSquads();
        String typeOfFile;
        if (user.getPatternSquads().isUseUserFile()) {
            typeOfFile = "Файл пользователя";
        } else {
            typeOfFile = "Файл сервера";
        }

        String format = "%25s : %40s";
        String[] headers = new String[]{"Тип коллекции", "Размер коллекции", "Тип файла"};
        String[] values = new String[]{collection.getClass().getName(), Integer.toString(collection.size()), typeOfFile};


        StringJoiner info = new StringJoiner("\n");

        for (int i = 0; i < headers.length; i++) {
            info.add(String.format(format, headers[i], values[i]));
        }
        return info.toString();
    }


    private String importing() {
        if (packet.getAttachment() == null) {
            return "Нечего импортировать";
        } else {
            String fileContent = (String) packet.getAttachment();
            Main.logger.fine(fileContent);
            user.getPatternSquads().setUserContent(fileContent);
            try {
                user.getPatternSquads().setSquads();
            } catch (NoPersonsException e) {
                if (user.getPatternSquads().getSquads().size() == 0) {
                    return e.getMessage();
                } else {
                    StringJoiner joiner = new StringJoiner("\n");
                    for (Squad sq: user.getPatternSquads().getSquads()) {
                        joiner.add(sq.toString() + " имортирован");
                    }
                    return joiner.toString() + "\n" + e.getMessage();
                }
            }
            return "Файл успешно импортирован";
        }
    }

    private String get() throws IOException {
        SquadParam params = parseJson(arguments);
        Squad squad;
        if (params.getName() != null && params.getMembers() != null) {
            try {
                Squad in = user.getPatternSquads().
                        formSquad(params.getName(), params.getMembers());
                squad = user.getPatternSquads().getSquads().stream().filter(s -> s.equals(in)).
                        findAny().orElse(null);
            } catch (NoPersonsException e) {
                connector.sender.sendObj(new Empty());
                return e.getMessage();
            }
        } else {
            if (params.getName() != null) {
                squad = user.getPatternSquads().getSquads().
                        stream().filter(s -> s.getName().equals(params.getName())).findAny().orElse(null);
            } else {
                squad = null;
            }
        }

        if (squad != null) {
            squad.getCosmonauts()[0].setSome(100);
            connector.sender.sendObj(squad);
        } else {
            connector.sender.sendObj(new Empty());
            return "Отряд " + arguments + " не найден";
        }
        return "Отряд " + squad.toString() + " найден и отправлен";
    }


    private String load() {
        try {
            user.getPatternSquads().readFile();
            user.getPatternSquads().setSquads();
            return "Коллекция успешно загружена";
        } catch (NoPersonsException e) {
            return e.getMessage();
        }


    }

    private String save() {
        if (!user.getPatternSquads().isUseUserFile()) {
            user.getPatternSquads().save();
            return "Успешно сохранено на сервер";
        } else {
            user.getPatternSquads().save();
            try{
                connector.sender.sendString(user.getPatternSquads().getUserFileContent());
                return "Файл отправлен пользователю";
            } catch (IOException e) {
                e.printStackTrace();
                return "Что-то пошло не так";
            }
        }
    }

    private String remove_first() {
        if (user.getPatternSquads().getSquads().size() != 0) {
            Squad squad = user.getPatternSquads().getSquads().remove(0);
            user.getPatternSquads().save();
            return "Удален отряд " + squad.toString();
        } else {
            return "В коллекции нет отрядов";
        }
    }

    private String remove_last() {
        if (user.getPatternSquads().getSquads().size() != 0) {
            int lastIndex = user.getPatternSquads().getSquads().size() - 1;
            Squad squad = user.getPatternSquads().getSquads().remove(lastIndex);
            user.getPatternSquads().save();
            return "Удален отряд " + squad.toString();
        } else {
            return "В коллекции нет отрядов";
        }
    }

    private String add() {
        SquadParam params = parseJson(arguments);
        if (params.getName() == null) return "Необходимо указать имя отряда";
        if (params.getMembers() == null) return "Необходимо указать состав отряда";
        try {
            return "Добавлен отряд " + user.getPatternSquads().
                    addSquad(params.getName(), params.getMembers()).toString();
        } catch (NoPersonsException e) {
            return e.getMessage();
        }
    }

    private String remove() {
        if (user.getPatternSquads().getSquads().size() == 0) return "В коллекции нет отрядов";
        SquadParam params = parseJson(arguments);
        if (params.getName() == null) return "Необходимо указать имя отряда";
        if (params.getMembers() == null) {
            Squad sq = user.getPatternSquads().getSquads().stream().
                    filter(s -> s.getName().equals(params.getName())).findAny().orElse(null);
            if (sq == null) {
                return "Отряд с именем " + params.getName() + " не найден";
            } else {
                user.getPatternSquads().getSquads().remove(sq);
                return "Удален отряд " + sq.toString();
            }
        }
        try {
            return "Удален отряд " + user.getPatternSquads().
                    removeSquad(params.getName(), params.getMembers()).toString();
        } catch (NoPersonsException e) {
            return e.getMessage();
        }
    }

    private SquadParam parseJson(String str) {
        SquadParam params = new SquadParam();
        String name = getTagValue(str, "name", false);
        if (name != null) {
            params.setName(name);
        }
        String resString = getTagValue(str, "members", true);
        if (resString != null) {
            String[] members = resString.replace("\"", "").
                    replace(" ", "").split(",");
            params.setMembers(members);
        }
        return params;
    }

    private String getTagValue(String str, String tag, boolean array) {
        char endChar;
        if (array) {
            endChar = ']';
        } else {
            endChar = '"';
        }
        int begin = str.indexOf("\"" + tag + "\":") + tag.length() + 4;
        int end = str.indexOf(endChar, begin);
        if (begin == -1 || end == -1) return null;
        return str.substring(begin, end);
    }

    private String help() {
        StringJoiner joiner = new StringJoiner("\n");
        String format = "%25s : %70s";
        joiner.add(String.format(format, "add {element_full}", "Добавляет элемент в коллекцию"));
        joiner.add(String.format(format, "remove {element_s}", "Удаляет элемент из коллекции"));
        joiner.add(String.format(format, "get {element_s}", "Позволяет получить элемент с сервера"));
        joiner.add(String.format(format, "remove_first", "Удаляет первый элемент из коллекции"));
        joiner.add(String.format(format, "remove_last", "Удаляет последний элемент из коллекции"));
        joiner.add(String.format(format, "show", "Выводит индекс элемента, название и имена ченов отряда"));
        joiner.add(String.format(format, "info", "Выводит некоторую информацию о коллекции и файле"));
        joiner.add(String.format(format, "cont", "Продолжить выполнение программы"));
        joiner.add(String.format(format, "load", "Загрузить коллекцию из файла сервера"));
        joiner.add(String.format(format, "save", "Сохраняет коллекцию, если работа с файлом сервера - на сервер"));
        joiner.add(String.format(format, "","если работа с файлом пользователя - в файл пользователя"));
        joiner.add(String.format(format, "exit", "Завершает работу (Ваша сессия будет сохранена)"));
        joiner.add(String.format(format, "help", "Вывести эту справку"));
        joiner.add(String.format(format, "import", "Загружает файл с коллекцией от клиента на сервер"));
        joiner.add(String.format(format, "connect", "Переподключение к серверу"));
        joiner.add(String.format(format, "sort", "Отсортировать отряды по имени"));
        joiner.add(String.format(format, "{element_full}", "{\"name\":\"Имя отряда\", \"members\":[\"Имя1\", \"Имя2\"]}"));
        joiner.add(String.format(format, "{element_s}", "{\"name\":\"Имя отряда\"} (Обращение к элементу только по имени)"));
        return joiner.toString();
    }

    private String sort(){
        if (user.getPatternSquads().getSquads().size() == 0) {
            return "Нечего сортировать";
        } else {
            user.getPatternSquads().getSquads().sort(Comparator.comparing(Squad::getName));
            user.getPatternSquads().save();
            return "Коллекция отсортирована";
        }
    }

}