package history;

import java.io.Serializable;

public abstract class Creature implements Serializable{
    private int some;

    public void setSome(int some){
        this.some = some;
    }
    public int getSome(){
        return some;
    }

    public abstract void say(String str);
}








