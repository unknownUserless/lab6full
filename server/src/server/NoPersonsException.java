package server;

import history.NoSuchPersonException;

import java.util.ArrayList;
import java.util.StringJoiner;

public class NoPersonsException extends Exception {

    private ArrayList<NoSuchPersonException> noPersons;

    public NoPersonsException(){
        noPersons = new ArrayList<>();
    }

    public void addNoSuchPersonException(NoSuchPersonException e){
        this.noPersons.add(e);
    }

    @Override
    public String getMessage(){
        StringJoiner joiner = new StringJoiner(", ");
        for (NoSuchPersonException e : noPersons){
            joiner.add(e.getName());
        }
        return "Людей " + joiner.toString() + " не существует";
    }

}
