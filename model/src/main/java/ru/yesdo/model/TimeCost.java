package ru.yesdo.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by lameroot on 26.04.15.
 */
public class TimeCost {

    public final static String SPATIAL_LAYER_NAME = "timecost";
    public final static String TIME_COST_RELATIONSHIP_NAME = "TIME_COST";
    public final static String TIME_COST_RELATIONSHIP_COST_PARAM_NAME = "cost";

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

        public static SpecialDay byDate(Calendar calendar) {
            int i = calendar.get(Calendar.DAY_OF_WEEK);
            for (SpecialDay days : values()) {
                if ( days.ordinal() + 1 == i ) return days;
            }
            return null;
        }

    }

    private Double startDay;
    private Double finishDay;
    private Double startTime;
    private Double finishTime;

    private Date start;
    private Date finish;

    public Date getStart() {
        return start;
    }

    public Date getFinish() {
        return finish;
    }

    private Coordinate[] coordinates;
    private HashMap<String,Object> params = new HashMap<>();

    public static TimeCost duringOneSpecialDay(SpecialDay specialDay, Double startTime, Double finishTime, Long cost) {
        return new TimeCost(toDay(specialDay),null,startTime,finishTime,cost);
    }
    @Deprecated
    public static TimeCost duringOneDay(Calendar day, Double startTime, Double finishTime,Long cost) {
        return new TimeCost(toDay(day),null,startTime,finishTime,cost);
    }
    public static TimeCost duringOneDay(Calendar day, Calendar startTime, Calendar finishTime,Long cost) {
        return new TimeCost(toDay(day),null,toTime(startTime),toTime(finishTime),cost);
    }
    public static TimeCost duringSeveralDays(Calendar startDay, Calendar finishDay, Double startTime, Double finishTime, Long cost) {
        return new TimeCost(toDay(startDay),toDay(finishDay),startTime,finishTime,cost);
    }

    public static Double toDay(Calendar date) {
        return Double.parseDouble(String.valueOf(date.get(Calendar.YEAR)) + StringUtils.leftPad(String.valueOf(date.get(Calendar.DAY_OF_YEAR)), 3, '0'));
    }
    public static Double toTime(Calendar date) {
        int minuteOfDay = date.get(Calendar.HOUR_OF_DAY) * 60 + date.get(Calendar.MINUTE);
        System.out.println("minuteOfDay = " + minuteOfDay);
        System.out.println("Double.parseDouble(String.valueOf(StringUtils.leftPad(String.valueOf(minuteOfDay),2,'0'))) = " + Double.parseDouble(String.valueOf(StringUtils.leftPad(String.valueOf(minuteOfDay),2,'0'))));
        return Double.parseDouble(String.valueOf(StringUtils.leftPad(String.valueOf(minuteOfDay),2,'0')));
    }
    public final static Date toDate(Double day, Double time) {
        String sDay = String.valueOf(day.intValue());
        String sYear = sDay.substring(0,sDay.length()-3);
        String sDayOfYear = sDay.substring(sDay.length()-3);

        String sMin = String.valueOf(time.intValue());
        Integer minInDay = Integer.valueOf(sMin);

        Integer hour = (minInDay / 60);
        Integer minInHour = minInDay - ( hour * 60);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR,Integer.valueOf(sYear));
        date.set(Calendar.DAY_OF_YEAR,Integer.valueOf(sDayOfYear));
        date.set(Calendar.HOUR_OF_DAY,hour);
        date.set(Calendar.MINUTE,minInHour);
        date.set(Calendar.SECOND,0);

        return date.getTime();
    }

    public static void main(String[] args) {
        Double d = 1.22;
        System.out.println(d.intValue());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,0);
        System.out.println(calendar.getTime());
        Double day2 = toDay(calendar);
        Double time2 = toTime(calendar);
        System.out.println("day2 = " + day2);
        System.out.println("time2 = " + time2);
        Date date = TimeCost.toDate(day2, time2);
        System.out.println(date);
    }

    protected TimeCost(Double startDay, Double finishDay, Double startTime, Double finishTime, Long cost) {
        this.startDay = startDay;
        if ( null != finishDay ) this.finishDay = finishDay;
        else this.finishDay = this.startDay;
        this.startTime = startTime;
        if ( null != finishTime ) this.finishTime = finishTime;
        else this.finishTime = this.startTime;
        addCost(cost);
    }
    private TimeCost() {

    }


    public final static Double toDay(SpecialDay specialDay) {
        return new Double("-0." + specialDay.num);
    }


    private void addCost(Long cost) {
        params.put(TIME_COST_RELATIONSHIP_COST_PARAM_NAME,cost);
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

    //todo: сюда надо передавать не Double а правильный формат. над опереписать и уйти от этого типа, слишком ненадёжный
    public static Geometry createBox(GeometryFactory geometryFactory, Calendar startDay, Calendar finishDay, Double startTime, Double finishTime) {
        Double sd = toDay(startDay);
        Double fd = toDay(finishDay);
        Coordinate leftLow = new Coordinate(sd,startTime);
        Coordinate rightLow = new Coordinate(fd,startTime);
        Coordinate leftHigh = new Coordinate(sd, finishTime);
        Coordinate rightHigh = new Coordinate(fd, finishTime);
        return geometryFactory.createPolygon(new Coordinate[]{leftLow,leftHigh,rightHigh,rightLow,leftLow});
    }
    public static TimeCost fromGeometry(Geometry geometry) {
        TimeCost timeCost = new TimeCost();
        if ( geometry instanceof LineString ) {
            LineString lineString = (LineString)geometry;
            Coordinate startPoint = lineString.getStartPoint().getCoordinate();
            Coordinate endPoint = lineString.getEndPoint().getCoordinate();
            double yearDayStart = startPoint.x;
            double hourMinStart = startPoint.y;
            double yearDayEnd = endPoint.x;
            double hourMinEnd = endPoint.y;

            timeCost.start = toDate(yearDayStart, hourMinStart);
            timeCost.finish = toDate(yearDayEnd, hourMinEnd);
        }
        return timeCost;
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
    public static Calendar createDateTime(Integer hour, Integer min) {
        if ( null == hour ) throw new IllegalArgumentException("Hour must be set");
        if ( null == min ) min = 0;
        if ( hour < 0 || hour >= 24 ) throw new IllegalArgumentException("Hour must be between 0 and 23 hours");
        if ( min < 0 || min >= 60 ) throw new IllegalArgumentException("Min must be between 0 and 59 minutes");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);

        return calendar;
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

    private static final Logger logger = LoggerFactory.getLogger(TimeCost.class);

    public boolean isValid(boolean throwException) {
        String error = null;
        if ( null == params || params.isEmpty() ) {
            error = "Params is empty";
        }
        if ( !params.containsKey(TIME_COST_RELATIONSHIP_COST_PARAM_NAME) ) {
            error = "Param: " + TIME_COST_RELATIONSHIP_COST_PARAM_NAME + " is absent";
        }
        boolean status = true;
        if ( StringUtils.isNotBlank(error)) {
            logger.warn(error);
            status = false;
        }
        if ( !status && throwException ) throw new IllegalArgumentException(error);
        return status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimeCost{");
        sb.append("startDay=").append(startDay);
        sb.append(", finishDay=").append(finishDay);
        sb.append(", startTime=").append(startTime);
        sb.append(", finishTime=").append(finishTime);
        sb.append("start=").append(start);
        sb.append(", finish=").append(finish);
        sb.append('}');
        return sb.toString();
    }

    public String toStringShort() {
        final StringBuilder sb = new StringBuilder("TimeCost{");
        sb.append("start=").append(start);
        sb.append(", finish=").append(finish);
        sb.append('}');
        return sb.toString();
    }
}
