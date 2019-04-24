package history;

class NoProf extends NullPointerException {
    NoProf(String name){
        super("У "+ name + " нет профессии");
    }
    public void whatHappened(){
        System.err.println(getMessage());
    }
}