package ru.yesdo.model;

/**
 * Created by lameroot on 13.12.14.
 * Класс для определения место положения чего-бы то не было
 * !!! на данном этапе использовать только как заглушечную модель, должна хранится в БД как справочная инфа
 */
public class Location {

    private String wkt;
    private String address;

    public void setLocation(double lon, double lat) {
        this.wkt = String.format("POINT( %s %s )",lon,lat).replace(",",".");
    }

    public String getLocation() {
        return wkt;
    }
}
