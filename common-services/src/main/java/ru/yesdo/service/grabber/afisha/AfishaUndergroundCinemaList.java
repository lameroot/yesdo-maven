package ru.yesdo.service.grabber.afisha;

import ru.yesdo.model.data.GeoData;

/**
 * Created by lameroot on 08.03.15.
 */
class AfishaUndergroundCinemaList {
    String title;
    String url;
    GeoData geoData;


    public AfishaUndergroundCinemaList(String title, String url) {
        this.title = title.toLowerCase();
        this.url = url;


    }

    public AfishaUndergroundCinemaList setGeoData(GeoData geoData) {
        this.geoData = geoData;
        return this;
    }

    public GeoData getGeoData() {
        return geoData;
    }
}
