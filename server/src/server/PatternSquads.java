package server;

import history.NoSuchPersonException;
import history.Squad;
import history.PeopleMap;
import history.Human;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PatternSquads implements Serializable{

    private File fileName = Main.file;
    private String userFileContent;
    private String currentContent;
    private CopyOnWriteArrayList<Squad> squads = new CopyOnWriteArrayList<>();
    private String headers;
    private boolean useUserFile;
    private PeopleMap allPeople;
    private Lock lock = new ReentrantLock();

    public boolean isUseUserFile() {
        return useUserFile;
    }
    public CopyOnWriteArrayList<Squad> getSquads() {
        return squads;
    }
    public PatternSquads(PeopleMap map) throws NoPersonsException{
        this.allPeople = map;
        readFile();
        setSquads();
    }


    public void save() {
        StringBuilder content = new StringBuilder();
        content.append(headers).append("\n");
        for (Squad squad : squads) {
            StringJoiner names = new StringJoiner(", ");
            for (Human h : squad.getCosmonauts()) {
                names.add(h.getName());
            }
            content.append(squad.getName()).append(", ").
                    append("\"").append(names.toString()).append("\"").append("\n");
        }
        content.deleteCharAt(content.length() - 1);
        currentContent = content.toString();
        if (useUserFile) {
            this.userFileContent = content.toString();
        } else {
            saveToFile();
        }

    }

    private void saveToFile() {
        if (useUserFile) throw new RuntimeException("YOU USE USER FILE");
        lock.lock();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append(currentContent).flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void readFile() {
        useUserFile = false;
        lock.lock();
        try (Scanner scanner = new Scanner(fileName)) {
            StringJoiner joiner = new StringJoiner("\n");
            while (scanner.hasNextLine()) {
                joiner.add(scanner.nextLine());
            }
            this.currentContent = joiner.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public String getUserFileContent() {
        return userFileContent;
    }

    public void setUserContent(String userFileContent) {
        this.useUserFile = true;
        this.currentContent = userFileContent;
        this.userFileContent = userFileContent;
    }

    public void setSquads() throws NoPersonsException {
        ArrayList<String> strings = new ArrayList<>();
        Scanner scanner = new Scanner(currentContent);
        if (scanner.hasNextLine()) {
            headers = scanner.nextLine();
        }
        while (scanner.hasNextLine()) {
            strings.add(scanner.nextLine());
        }

        squads = new CopyOnWriteArrayList<>();
        for (String str : strings) {
            StringBuilder squadName = new StringBuilder();
            int i = 0;
            while (str.charAt(i) != ',') {
                squadName.append(str.charAt(i));
                i++;
            }
            String string = str.substring(i + 2);
            String[] names = string.replace("\"", "").
                    replace(" ", "").split(",");
            this.addSquad(squadName.toString(), names);
        }
        if (squads.size() == 0) {
            Main.logger.info("Squads size = 0, check file " + fileName.getAbsolutePath());
        }
    }

    public Squad addSquad(String squadName, String... names) throws NoPersonsException{
        Squad squad = formSquad(squadName, names);
        squads.add(squad);
        save();
        return squad;
    }

    public Squad removeSquad(String squadName, String... names) throws NoPersonsException{
        Squad squad = formSquad(squadName, names);
        squads.remove(squad);
        save();
        return squad;
    }

    public Squad formSquad(SquadParam param) throws NoPersonsException{
        return this.formSquad(param.getName(), param.getMembers());
    }

    public Squad formSquad(String squadName, String... names) throws NoPersonsException {
        List<Human> squadMembers = new ArrayList<>();
        NoPersonsException exception = null;
        for (String str : names) {
            try {
                squadMembers.add(allPeople.get(str));
            } catch (NoSuchPersonException e) {
                if (exception == null) {
                    exception = new NoPersonsException();
                }
                exception.addNoSuchPersonException(e);
            }
        }
        if (exception != null) throw exception;
        return new Squad(squadName, squadMembers.toArray(new Human[0]));
    }
}
