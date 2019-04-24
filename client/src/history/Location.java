package history;

import java.io.Serializable;

public class Location implements Serializable {
    private String locationName;

    public Location(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }
}