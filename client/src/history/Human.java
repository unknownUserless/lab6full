package history;


public class Human extends Creature implements IHuman {
    public enum Profession {
        MECHANIC,
        PROFESSOR,
        ASTRONOMER,
        ENGINEER,
        ARCHITECT,
        ARTIST,
        MUSICIAN,
        DOCTOR
    }

    private String name;
    private Profession prof;
    private Profession ability;

    public Profession getProf() {
        return prof;
    }

    public String getName() {
        return name;
    }

    public Human.Profession getAbility() {
        return ability;
    }

    public Human(String name) {
        this.name = name;
    }

    public Human(String name, Profession ability) {
        this.name = name;
        this.ability = ability;
    }

    @Override
    public int hashCode() {
        if (prof != null) {
            return name.hashCode() + prof.hashCode();
        } else return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Human) || o.hashCode() != this.hashCode()) {
            return false;
        } else {
            Human h = (Human) o;
            return h.getName().equals(this.getName()) && h.getProf() == this.getProf();
        }
    }

    @Override
    public void say(String str) {
        System.out.println(name + " say: " + str);
    }

    @Override
    public void respond() {
        if (prof != null) {
            System.out.println(prof + " " + name + " здесь");
        } else {
            System.out.println(name + " здесь");
        }
    }
}

