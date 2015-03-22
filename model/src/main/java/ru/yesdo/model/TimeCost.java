package ru.yesdo.model;

import com.vividsolutions.jts.geom.*;
import javafx.geometry.Point3D;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by lameroot on 15.03.15.
 */
public class TimeCost {

    public final static String SPATIAL_LAYER_NAME = "timecost";
    public final static String TIME_COST_RELATIONSHIP_NAME = "TIME_COST";
    public final static String TIME_COST_RELATIONSHIP_COST_PARAM_NAME = "cost";

//    private Double day;//x
//    private Double time;//y
    private Integer order;//порядок сортировки, если н-р есть что-то типа счастливых часов, то у этих фигур порядок больше и они выдаются первые
    private Long cost;
    private Double startDay;
    private Double finishDay;
    private Double startTime;
    private Double finishTime;
    private Coordinate[] coordinates;
    private HashMap<String,Object> params = new HashMap<>();

    public static enum SpecialDay {
        SUNDAY(1),
        MONDAY(2),
        TUESDAY(3),
        WEDNESDAY(4),
        THURSDAY(5),
        FRIDAY(6),
        SATURDAY(7);

        private int num;

        SpecialDay(int num) {
            this.num = num;
        }
    }

    public static TimeCost duringOneSpecialDay(SpecialDay specialDay, Double startTime, Double finishTime, Long cost) {
        return new TimeCost(toDay(specialDay),null,startTime,finishTime,cost);
    }
    public static TimeCost duringOneDay(Calendar day, Double startTime, Double finishTime,Long cost) {
        return new TimeCost(day,null,startTime,finishTime,cost);
    }
    public static TimeCost duringSeveralDays(Calendar startDay, Calendar finishDay, Double startTime, Double finishTime, Long cost) {
        return new TimeCost(startDay,finishDay,startTime,finishTime,cost);
    }

    protected TimeCost(Calendar day, Double startTime, Double finishTime, Long cost) {
        this(day,null,startTime,finishTime,cost);
    }
    protected TimeCost(Calendar startDay, Calendar finishDay, Double startTime, Double finishTime, Long cost) {
        this.startDay = toDay(startDay);
        this.finishDay = toDay(finishDay);
        if ( null == finishDay ) this.finishDay = this.startDay;
        this.startTime = startTime;
        this.finishTime = finishTime;
        if ( null == finishTime ) this.finishTime = this.startTime;
        this.cost = cost;
        addCost(cost);
    }
    protected TimeCost(Double startDay, Double finishDay, Double startTime, Double finishTime, Long cost) {
        this.startDay = startDay;
        this.finishDay = finishDay;
        if ( null == finishDay ) this.finishDay = this.startDay;
        this.startTime = startTime;
        this.finishTime = finishTime;
        if ( null == finishTime ) this.finishTime = this.startTime;
        this.cost = cost;
    }
    public TimeCost(Coordinate...coordinates) {
        this.coordinates = coordinates;
    }
    //тут надо составлять полигон по всем точкам
//    public TimeCost(Calendar startDay, Calendar finishDay, Double startTime, Double finishTime,  Double startTime2, Double finishTime2, Double cost) {
//
//    }

    private void addCost(Long cost) {
        params.put(TIME_COST_RELATIONSHIP_COST_PARAM_NAME,cost);
    }

    public final Geometry toGeometry(GeometryFactory geometryFactory) {
        if ( null != coordinates && 0 < coordinates.length ) return createPolygon(geometryFactory);
        if ( isTheSameDay(startDay,finishDay) ) {
            if ( isTheSameTime(startTime,finishTime) ) {
                return createPoint(geometryFactory);
            }
            else {
                return createLine(geometryFactory);
            }
        }
        else {
            return createBox(geometryFactory);
        }
    }

    private boolean isTheSameDay(Double startDay, Double finishDay) {
        if ( startDay.equals(finishDay) ) {
            return true;
        }
        return false;
    }

    private boolean isTheSameTime(Double startTime, Double finishTime) {
        return startTime.equals(finishTime);
    }

    public final static Double toDay(Calendar date) {
        if ( null == date ) return null;
        return new Double(date.get(Calendar.YEAR) + "." + date.get(Calendar.DAY_OF_YEAR));
    }
    public final static Double toDay(SpecialDay specialDay) {
        return new Double("-0." + specialDay.num);
    }


    private Geometry createPoint(GeometryFactory geometryFactory) {
        return geometryFactory.createPoint(new Coordinate(startDay,startTime));
    }

    private Geometry createLine(GeometryFactory geometryFactory) {
        return geometryFactory.createLineString(new Coordinate[]{new Coordinate(startDay,startTime),new Coordinate(startDay,finishTime)});
    }

    private Geometry createBox(GeometryFactory geometryFactory) {
        Coordinate leftLow = new Coordinate(startDay,startTime);
        Coordinate rightLow = new Coordinate(finishDay,startTime);
        Coordinate leftHigh = new Coordinate(startDay, finishTime);
        Coordinate rightHigh = new Coordinate(finishDay, finishTime);
        return geometryFactory.createPolygon(new Coordinate[]{leftLow,leftHigh,rightHigh,rightLow,leftLow});
    }

    private Geometry createPolygon(GeometryFactory geometryFactory) {
        return geometryFactory.createPolygon(coordinates);
    }

    public HashMap getParamsOfRelationship() {
        return params;
    }

    public static Double createTime(Integer hour, Integer min) {
        if ( null == hour ) throw new IllegalArgumentException("Hour must be set");
        if ( null == min ) min = 0;
        if ( hour < 0 || hour >= 24 ) throw new IllegalArgumentException("Hour must be between 0 and 23 hours");
        if ( min < 0 || min >= 60 ) throw new IllegalArgumentException("Min must be between 0 and 59 minutes");
        return new Double(hour + "." + min);
    }

}
