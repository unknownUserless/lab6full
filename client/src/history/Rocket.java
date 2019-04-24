package history;

public class Rocket extends Location {
    private String name;
    private String nameFrom;

    public Rocket(String name, String nameFrom) {
        super(name);
        this.name = name;
        this.nameFrom = nameFrom;
    }

    public String getName() {
        return name;
    }

    public String getNameFrom() {
        return nameFrom;
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o){
    if (o == null || o.getClass() != this.getClass()) return false;
    else return ( o.hashCode() == this.hashCode() ) && ( ((Rocket)o).name.equals(this.name) );
    }

    @Override
    public String getLocationName() {
        return name;
    }
}
