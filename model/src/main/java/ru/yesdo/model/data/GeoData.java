package ru.yesdo.model.data;

/**
 * Created by lameroot on 25.02.15.
 */
public class GeoData {

    private String name;
    private String title;
    private double lon;
    private double lat;

    public GeoData(String name, String title, double lon, double lat) {
        this.name = name;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeoData{");
        sb.append("name='").append(name).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", lon=").append(lon);
        sb.append(", lat=").append(lat);
        sb.append('}');
        return sb.toString();
    }
}
