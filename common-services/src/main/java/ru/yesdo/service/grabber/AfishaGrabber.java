package ru.yesdo.service.grabber;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.data.ContactData;
import ru.yesdo.model.data.GeoData;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.service.GeoDataImporter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.print.Doc;
import java.io.IOException;
import java.util.*;

/**
 * Created by lameroot on 04.03.15.
 */
@Service
public class AfishaGrabber {

    private String url = "http://www.afisha.ru/msk/";
    private String cinemaListUrl = url + "cinemas/cinema_list/";

    @Resource
    private GeoDataImporter geoDataImporter;
    private List<GeoData> undergrounds;
    private Map<String,AfishaUndergroundCinemaList> undergroundCinemaListMap = new HashMap<>();


    @PostConstruct
    private void init() throws Exception {
        undergrounds = geoDataImporter.importUndergroundToList(new ClassPathResource("metro_stations_msk.csv"), ",", new GeoDataImporter.SplitMethod() {
            @Override
            public GeoData split(String[] ar) {
                return new GeoData(ar[1],ar[10],Double.parseDouble(ar[3]),Double.parseDouble(ar[4]));
            }
        });
        undergroundCinemaListMap = fillUnderground();
        for (GeoData underground : undergrounds) {
            String title = underground.getTitle().toLowerCase();
            title = title.substring(6).trim();
            if ( undergroundCinemaListMap.containsKey(title) ) {
                undergroundCinemaListMap.get(title).setGeoData(underground);
            }
        }
        for (Map.Entry<String, AfishaUndergroundCinemaList> entry : undergroundCinemaListMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().getGeoData());
        }
    }

    private class AfishaUndergroundCinemaList {
        private String title;
        private String url;
        private GeoData geoData;

        public AfishaUndergroundCinemaList(String title, String url) {
            this.title = title.toLowerCase();
            this.url = url;
        }

        public AfishaUndergroundCinemaList setGeoData(GeoData geoData) {
            this.geoData = geoData;
            return this;
        }

        public GeoData getGeoData() {
            return geoData;
        }
    }

    private Map<String,AfishaUndergroundCinemaList>  fillUnderground() throws IOException {
        Document document = Jsoup.connect(cinemaListUrl).get();

        Map<String,AfishaUndergroundCinemaList> undergroudCinemaListMap = new HashMap<>();
        Elements elements = document.select(".b-nav-filter");
        for (Element element : elements) {
            Elements elements1 = element.select("a");
            for (Element element1 : elements1) {
                if ( !"#".equals(element1.attr("href")) ) {
                    String url = element1.attr("href");
                    String title = null;
                    if ( StringUtils.isBlank(element1.attr("title")) ) {
                        title = element1.select("span").select(".name").text();
                    }
                    else {
                        title = element1.attr("title");
                    }
                    undergroudCinemaListMap.put(title.toLowerCase(),new AfishaUndergroundCinemaList(title,url));
                }
            }
        }
        System.out.println("size under = " + undergroudCinemaListMap.size());
        return undergroudCinemaListMap;
    }

    public void cinemasByUnderground(GeoData geoData) throws IOException {
        List<String> cinemasByUnderground = new ArrayList<>();

        Optional<AfishaUndergroundCinemaList> optional = undergroundCinemaListMap.values().stream().filter(u-> null != u.getGeoData() && u.getGeoData().equals(geoData)).findFirst();
        if ( optional.isPresent() ) {
            AfishaUndergroundCinemaList afishaUndergroundCinemaList = optional.get();
            String url = afishaUndergroundCinemaList.url;
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select(".places-list-item");
            for (Element element : elements) {
                String urlCinema = element.select("h3").select("a").attr("href");


            }
        }


    }

    private MerchantData cinema(String urlCinema) throws IOException {
        MerchantData merchantData = new MerchantData();

        Document documentCinema = Jsoup.connect(urlCinema).get();
        String cinemaName = documentCinema.select(".b-object-header").select("h1").text();
        merchantData.setTitle(cinemaName);
        merchantData.setName(cinemaName);//todo

        ContactData contactData = new ContactData();


        return merchantData;
    }


    public static void main(String[] args) throws IOException {
        AfishaGrabber grabber = new AfishaGrabber();
        grabber.fillUnderground();
    }
}
