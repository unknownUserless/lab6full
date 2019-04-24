package server;

public class SquadParam {
    private String name;
    private String[] members;

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public String[] getMembers() {
        return members;
    }
}
