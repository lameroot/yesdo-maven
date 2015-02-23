package ru.yesdo.model;

import java.util.Date;
import java.util.List;

/**
 * Created by lameroot on 13.12.14.
 * Время в которое действует продукт. То есть его время начало и конца
 */
public class TimeProduct {

    private Date start;//начало
    private Date finish;//окончание
    private Date autoProlong;//производить авто-пролонгирование продукта на определённую дату

/*
    public enum TimeTypes {
        SEASON_TIME,
        WEEKEND_TIME,
        WORKDAYS_TIME,
        ALLDAYS_TIME,
        CUSTOM_TIME;

        private List<TimeInterval> timeIntervals;

        TimeTypes(List<TimeInterval> timeIntervals) {
            this.timeIntervals = timeIntervals;
        }
    }
    */

    private static class TimeInterval {
        int startHour = 0;
        int finishHour = 23;
        int starMinute = 0;
        int finishMinute = 59;
    }

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

    /**
     * Выражение для временного интервала
     * 1. По дням недели: MONDAY:10-13,14-22;WEDNESDAY:10-13,14-22;FRIDAY:10-13,14-22;SATURDAY:11-17
     * 2. Вся рабочая неделя: WORKDAY:10-22;SATURDAY:11-21
     * 3. Вся неделя: ALL_WEEK:10-22
     * 4. Определённая дата: 22012015:10-22
     * 5. Диапазон определённых дат: 22012015-25012015:10-14,15-22
     * 6. Только один час: 22012015:14 или WEDNESDAY:15
     */
    private String timeExpression;
}
