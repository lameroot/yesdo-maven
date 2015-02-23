package ru.yesdo.model.data;

/**
 * Created by lameroot on 23.02.15.
 */
public class OfferTimeData {

    private Integer startTime = 0;
    private Integer finishTime = 2359;

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

    public OfferTimeData start(Integer hourMin) {
        if ( null == hourMin ) return this;
        this.startTime = hourMin;
        return this;
    }
    public OfferTimeData finish(Integer hourMin) {
        if ( null == hourMin ) return this;
        this.finishTime = hourMin;
        return this;
    }
    public OfferTimeData interval(Integer startTime, Integer finishTime) {
        start(startTime);
        finish(finishTime);
        return this;
    }
}
