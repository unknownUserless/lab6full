package history;

public class NoSuchPersonException extends NullPointerException{
    private String name;
    public NoSuchPersonException(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

}