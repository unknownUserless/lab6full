package history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.StringJoiner;

public class Squad implements ISquad, Comparable<Squad>, Serializable {

    private Location location;
    private String research;
    private Cosmonaut[] memCosmonauts;
    private String name;
    private Date birthday;
    private boolean exist;

    public Location getLocation() {
        return location;
    }

    public void moveTo(Location location) {
        this.location = location;
    }

    public Squad(String name, Human[] members) {
        this.memCosmonauts = new Cosmonaut[members.length];
        setCosmonauts(members);
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void toBorn(){
        if (!this.exist) {
            this.birthday = new Date();
            this.exist = true;
        } else {
            System.err.println("Отряд уже существует");
        }
    }

    public Date getBirthday(){
        return birthday;
    }

    public Cosmonaut[] getCosmonauts(){
        return memCosmonauts;
    }

    private void setCosmonauts(Human[] members){
            for (int i = 0; i<members.length; i++){
                this.memCosmonauts[i] = new Cosmonaut(members[i]);
            }
    }

    @Override
    public void explore(Rocket r) throws AllDied {
        System.out.println("Отряд начал исследование");
        int numDiedMen = 0;
        ArrayList<SomeoneDied> diedMen = new ArrayList<>();
        for (Cosmonaut cosmonaut:memCosmonauts) {
            try {
                if (cosmonaut.getProtection() < 50) throw new SomeoneDied("без скафандра тяжело дышать", cosmonaut);
                cosmonaut.explore(r);
            } catch (SomeoneDied e){
                diedMen.add(e);
                e.whatHappened();
                numDiedMen++;
            }
        }
        SomeoneDied[] dieds = new SomeoneDied[diedMen.size()];
        diedMen.toArray(dieds);
        if (numDiedMen == memCosmonauts.length) {
            AllDied ex = new AllDied("Умер отряд", dieds);
            ex.initCause(new SomeoneDied());
            throw ex;
        }

        research = "Рокета "+r.getName()+" , "+r.getNameFrom();

    }

    @Override
    public void report(){
        System.out.println("Отчет: "+research);
    }

    @Override
    public void putOnSuits(){
        for (Cosmonaut mem: memCosmonauts){
            mem.putOnSuit();
        }
    }

    public int size(){
        return memCosmonauts.length;
    }

    @Override
    public int compareTo(Squad other){
        return this.size()-other.size();
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(memCosmonauts);
    }

    @Override
    public boolean equals(Object o){
        if (o == null || o.getClass() != this.getClass()) return false;
        Squad other = (Squad)o;
        if (this.getCosmonauts().length != other.getCosmonauts().length) return false;
        for (int i = 0; i<getCosmonauts().length; i++){
            if (!this.getCosmonauts()[i].getName().equals(other.getCosmonauts()[i].getName())) return false;
        }
        return true;
    }

    @Override
    public String toString(){
        StringJoiner joiner = new StringJoiner(", ");
        Arrays.stream(memCosmonauts).forEach((h)->{
            joiner.add(h.getName());
        });
        return name + ": " + joiner.toString();
    }
}

