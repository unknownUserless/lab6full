package history;


public class Cosmonaut extends Human {
    private int protection;
    public int getProtection(){
        return protection;
    }
    public Cosmonaut(Human h){
        super(h.getName(), h.getProf());
    }
    public void explore(Rocket r){
        System.out.println("Космонавт "+getName()+" закончил исследование");
    }

    public void putOnSuit(){
        protection = 100;
    }
    public void takeOffSuit(){
        protection = 5;
    }
}