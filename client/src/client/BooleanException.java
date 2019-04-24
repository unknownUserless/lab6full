package client;

public class BooleanException extends RuntimeException {

    String value;

    public BooleanException(String str){
        this.value = str;
    }

    @Override
    public String getMessage(){
        return value + " cant be boolean value";
    }
}
