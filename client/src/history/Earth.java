package history;

import java.lang.reflect.Field;
import java.util.Map;

public class Earth extends Location {

    private static Earth earth;

    public static Earth getInstance(){
        if (earth == null){
            earth = new Earth();
        }
        return earth;
    }

    public final Factory factory;
    public final University university;
    public final MaternityHospital maternityHospital;

    public class Factory{
        public Spaceship getSpaceship(String locationName, Human capitan, PeopleMap crew){
            return new Spaceship(locationName, capitan, crew);
        }

        public Rocket getRocket(String name, String nameFrom){
            return new Rocket(name, nameFrom);
        }
    }

    public class MaternityHospital {
        public Human toBeBorn(String name){
            return new Human(name);
        }

        public Human toBeBorn(String name, Human.Profession prof){
            return new Human(name, prof);
        }
    }

    public static class University{
        public void teach(Human h, Human.Profession profession){
            try {
                Field prof = Human.class.getDeclaredField("prof");
                prof.setAccessible(true);
                prof.set(h, profession);
            } catch (NoSuchFieldException | IllegalAccessException e){
                e.printStackTrace();
            }
        }

        public void teachAllByAbility(Map<String, Human> students){
            for (Human h:students.values()){
                teach(h, h.getAbility());
            }
        }
    }

    private Earth(){
        super("Земля");
        this.maternityHospital = new MaternityHospital();
        this.factory = new Factory();
        this.university = new University();
    }
}