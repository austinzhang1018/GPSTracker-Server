package tools;

/**
 * Created by Austin on 3/20/2018.
 */
public class Location {
    double latitude;
    double longitude;
    float accuracy;

    public Location(double latitude, double longitude, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
