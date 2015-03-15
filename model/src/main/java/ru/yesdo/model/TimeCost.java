package ru.yesdo.model;

import javafx.geometry.Point3D;

import java.util.Calendar;

/**
 * Created by lameroot on 15.03.15.
 */
public class TimeCost extends Point3D {

    private double cost;
    private double day;
    private double time;

    public TimeCost(double x, double y, double z) {
        super(x, y, z);
        this.cost = x;
        this.day = y;
        this.time = z;
    }

    public TimeCost(double cost, Calendar day, double time) {
        super(cost, (double)day.get(Calendar.DAY_OF_YEAR),time);
        this.cost = getX();
        this.day = getY();
        this.time = getZ();
    }
}
