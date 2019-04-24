package server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private File file;

    private Map<Integer, User> users = new HashMap<>();

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Users() {
        this.file = new File("users.ser");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
                clearFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void clearFile(){
        ss(new HashMap());
    }

    public boolean exists(){
        return this.file.exists();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        save();
    }

    public void save(){
        this.ss(users);
    }

    private void ss(Map<Integer, User> map) {
        try (FileOutputStream fos = new FileOutputStream(file, false);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(map);
            Main.logger.info("Сохранено " + users.size());
            fos.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        try(FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis)){
            this.users = (HashMap) ois.readObject();
            Main.logger.fine("Загружено " + users.size());
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InvalidClassException e){
            clearFile();
            load();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
