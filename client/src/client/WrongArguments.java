package client;


public class WrongArguments {
    private String args;
    private String command;

    public WrongArguments(String args, String command) {
        this.command = command;
        this.args = args;
    }

    public void info(){
        System.err.println("Аргумент " + args + " является недопустимым для команды " + command);
    }

}
