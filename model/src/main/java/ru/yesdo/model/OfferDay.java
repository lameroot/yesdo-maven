package ru.yesdo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lameroot on 23.02.15.
 */
public class OfferDay {

    public enum Days {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        WORKDAY,
        ALL_WEEK,
        WEEKEND,
        ALL_MONTH,
        ALL_YEAR

    }

    private static class TimeInterval {
        int startHour = 0;
        int finishHour = 23;
        int starMinute = 0;
        int finishMinute = 59;
    }

    private Days day;
    private List<TimeInterval> timeIntervals = new ArrayList<>();
}
