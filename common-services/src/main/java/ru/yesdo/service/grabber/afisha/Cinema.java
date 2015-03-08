package ru.yesdo.service.grabber.afisha;

import ru.yesdo.model.data.MerchantData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lameroot on 08.03.15.
 */
public class Cinema extends MerchantData {

    Integer id;
    String mapUrl;
    String regexp = "^http://www.afisha.ru/(\\w+)/cinema/(\\d+)/$";
    Pattern pattern = Pattern.compile(regexp);
    String city;
    String scheduleUrl;
    String url;
    String context;
    long startPrice;
    long finishPrice;

    public Cinema(String url) {
        this.url = url;
        Matcher matcher = pattern.matcher(url);
        if ( matcher.matches() ) {
            this.city = matcher.group(1);
            this.id = Integer.parseInt(matcher.group(2));
        }

        this.context = "http://www.afisha.ru/" + city + "/";
        this.mapUrl = this.context + "cinema/" + "map/" + id + "/";
        this.scheduleUrl = this.context + "schedule_cinema_place/" + id + "/";
    }

}
