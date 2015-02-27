package ru.yesdo.model.data;

import org.springframework.data.geo.Shape;

/**
 * User: Krainov
 * Date: 27.02.2015
 * Time: 18:34
 */
public class WithinDistance implements Shape {
    final double lat, lon, distanceKm;

    public WithinDistance(double lat, double lon, double distanceKm) {
        this.lat = lat;
        this.lon = lon;
        this.distanceKm = distanceKm;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getDistanceKm() {
        return distanceKm;
    }
}