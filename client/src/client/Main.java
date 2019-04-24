package client;

import history.*;

import java.io.File;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static PeopleMap allPeople = new PeopleMap();
    private static Map<String, Location> locations = new HashMap<>();

    private static File file;
    private static Console console;
    private static Connector connector;
    private static int userID;
    private static int serverMainPort;
    private static CommandManager manager;

    public static void main(String[] args) throws Exception {

        try {
            file = new File(System.getenv("file"));
        } catch (NullPointerException e) {
            System.err.println("Переменная окружения file не найдена,\n" +
                    "воспользуйтесь export file=\"path\", где path - путь к вашему файлу");
            System.exit(3);
        }

        if (!file.exists()) {
            System.err.println("Файл " + file.getAbsolutePath() + " не найден, создайте файл и запустите клиент заново");
            System.exit(4);
        }

        create();

        //////////////////////////////////

        System.out.println("Необходимо установить соединение с сервером");

        console = new Console();
        connector = new Connector();

        serverMainPort = console.readMainPort();

        userID = console.readId();

        connect();


        //Установка соединения
        //Необходимо получить отряд с сервера


        while (manager.getSquad() == null) {
            System.out.println("Необходимо получить отряд, для исследования рокеты\n" +
                    "Воспользуйтесь командой get squad, где squad - объект в формате json\n" +
                    "Для более детальной информации воспользуйтесь командой help");
            manager.start();
        }

        history();
        manager.start();
    }

    public static File getFile() {
        return file;
    }

    public static void connect() {
        MainPacket packet = new MainPacket(allPeople, userID, connector.getSocketAddress());
        try {
            connector.sendPackToServer(new InetSocketAddress(InetAddress.getLocalHost(), serverMainPort), packet);
            ServerRespondPacket pack = (ServerRespondPacket)connector.receiver.receiveObj();
            connector.setUserSenderAddress(pack.address);
            System.out.println("Соединение с сервером установлено");
            if (manager == null) {
                manager = new CommandManager(connector, console, pack.useUserFile);
            }
        } catch (SocketTimeoutException e){
            System.out.println("Соединение не было установлено");
            System.out.println("Чтобы повторить попытку соединения используйте команду connect\n" +
                    "Чтобы выйти команду exit");
            if (console.readCommand().equals("exit")){
                System.exit(2);
            } else {
                connect();
            }
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            System.exit(-100);
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
    }

    private static void create(){
        locations.put("НИП", Earth.getInstance().factory.getRocket("НИП",
                "так условились сокращенно называть ракету," +
                        " на которой прилетели Незнайка и Пончик"));
        locations.put("ФИС", Earth.getInstance().factory.getRocket("ФИС",
                "которую решили сокращенно называть" +
                        " по имени главных ее конструкторов Фуксии и Селедочки"));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Знайка"));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Фуксия"));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Селедочка"));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Винтик", Human.Profession.MECHANIC));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Шпунтик", Human.Profession.MECHANIC));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Звездочкин", Human.Profession.PROFESSOR));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Стекляшкин", Human.Profession.ASTRONOMER));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Клепка", Human.Profession.ENGINEER));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Кубик", Human.Profession.ARCHITECT));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Тюбик", Human.Profession.ARTIST));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Гусля", Human.Profession.MUSICIAN));
        allPeople.add(Earth.getInstance().maternityHospital.toBeBorn("Пилюлькин", Human.Profession.DOCTOR));

        Human yoda = new Human("Йода") {
            @Override
            public void say(String str) {
                String[] words = str.split(" ");
                System.out.print(getName() + ": ");
                for (int i = words.length - 1; i >= 0; i--) {
                    System.out.print(words[i] + " ");
                }
                System.out.println();
            }
        };
        allPeople.add(yoda);

        Earth.getInstance().university.teachAllByAbility(allPeople);
        locations.put("Spaceship", Earth.getInstance().factory.getSpaceship("Spaceship", allPeople.get("Знайка"), allPeople.getSome("Фуксия", "Селедочка",
                "Винтик", "Шпунтик", "Звездочкин", "Стекляшкин", "Клепка", "Кубик", "Тюбик", "Гусля", "Пилюлькин")));
    }

    private static void history(){
        Spaceship spaceship = (Spaceship) locations.get("Spaceship");

        System.out.println(spaceship.toString());
        spaceship.landing();
        spaceship.setSquad(manager.getSquad());
        spaceship.squadExplore((Rocket)locations.get("НИП"));
        spaceship.disbandSquad();
        spaceship.takeoff();
    }
}
