package history;

import history.Human;

public class SomeoneDied extends Exception{
    private Human diedMan;
    SomeoneDied(){

    }
    SomeoneDied(String why){
        super(why);
    }
    SomeoneDied(String why, Human who){
        this(why);
        diedMan = who;
    }
    public Human getDiedMan(){
        return diedMan;
    }

    public void whatHappened(){
        if (diedMan == null) {
            System.err.println("Кто-то умер, потому что " + getMessage());
        }  else {
            System.err.printf("Умер %s , потому что %s\n", diedMan.getName(), getMessage());
        }
    }
}
