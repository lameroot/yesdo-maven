package ru.yesdo.model;

import org.springframework.data.neo4j.annotation.*;

/**
 * Created by lameroot on 23.02.15.
 */
@RelationshipEntity(type = "OFFER_TIME")
public class OfferTime {

    @GraphId
    private Long id;

    private @StartNode WeekDay weekDay;
    private @EndNode Offer offer;
    @GraphProperty
    private Integer startTime = 0;
    @GraphProperty
    private Integer finishTime = 2359;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WeekDay getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Integer finishTime) {
        this.finishTime = finishTime;
    }

    public OfferTime start(Integer hourMin) {
        if ( null == hourMin ) return this;
        this.startTime = hourMin;
        return this;
    }
    public OfferTime finish(Integer hourMin) {
        if ( null == hourMin ) return this;
        this.finishTime = hourMin;
        return this;
    }
    public OfferTime day(WeekDay.Days day) {
        if ( null == day ) return this;
        this.weekDay = new WeekDay(day);
        return this;
    }
    public OfferTime interval(Integer startTime, Integer finishTime) {
        start(startTime);
        finish(finishTime);
        return this;
    }
}
