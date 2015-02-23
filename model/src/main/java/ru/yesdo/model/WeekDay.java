package ru.yesdo.model;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;

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
        ALL_YEAR

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
