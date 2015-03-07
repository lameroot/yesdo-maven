package ru.yesdo.model.data;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    public OfferTimeData start(Date date) {
        String s = new SimpleDateFormat("HHmm").format(date);
        return start(Integer.parseInt(s));
    }

    public OfferTimeData finish(Date date) {
        String s = new SimpleDateFormat("HHmm").format(date);
        return finish(Integer.parseInt(s));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OfferTimeData{");
        sb.append("startTime=").append(startTime);
        sb.append(", finishTime=").append(finishTime);
        sb.append('}');
        return sb.toString();
    }
}
