package ru.yesdo.model;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;

import java.util.Calendar;

/**
 * Created by lameroot on 23.02.15.
 */
@NodeEntity
public class WeekDay {

    public final static String INDEX_NAME = "week_days";
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
        ALL_YEAR;

        public static Days byDate(Calendar calendar) {
            int i = calendar.get(Calendar.DAY_OF_WEEK);
            for (Days days : values()) {
                if ( days.ordinal() + 1 == i ) return days;
            }
            return null;
        }

    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,5);
        System.out.println(calendar.get(Calendar.MONTH));
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));

        Days days = Days.byDate(Calendar.getInstance());
        System.out.println(days);
    }

    @GraphId
    private Long graphId;

    @Indexed(indexType = IndexType.LABEL, unique = true)
    private Days day;

    public WeekDay() {

    }

    public WeekDay(Days day) {
        this.day = day;
    }

    public Days getDay() {
        return day;
    }

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public void setDay(Days day) {
        this.day = day;
    }
}
