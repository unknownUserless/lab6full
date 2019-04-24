package history;

public class AllDied extends Exception{
    private SomeoneDied[] diedMen;

    AllDied(String why, SomeoneDied[] diedMen){
        this(why);
        this.diedMen = diedMen;
    }


    AllDied(String why){
        super(why);
    }
    public void whatHappened(){
        System.err.println("все умерли, потому что " + getMessage());
    }

    public void whoAndWhy(){
        for (SomeoneDied sm: diedMen){
            System.err.println(sm.getDiedMan().getName() + " " + sm.getMessage());
        }
    }
}