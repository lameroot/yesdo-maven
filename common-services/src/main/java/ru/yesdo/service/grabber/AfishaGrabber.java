package ru.yesdo.service.grabber;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.yesdo.model.*;
import ru.yesdo.model.data.*;
import ru.yesdo.service.GeoDataImporter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static class AfishaUndergroundCinemaList {
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

    private static class AfishaCinema {
        private String url;
        private String context;
        private Integer id;
        private String mapUrl;
        private String regexp = "^http://www.afisha.ru/(\\w+)/cinema/(\\d+)/$";
        private Pattern pattern = Pattern.compile(regexp);
        private String city;
        private String scheduleUrl;

        public AfishaCinema(String url) {
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

    private static class AfishaMerchantData {
        MerchantData merchantData;
        Map<Movie, OfferData> movieSetMap = new HashMap<>();


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
                AfishaCinema afishaCinema = new AfishaCinema(urlCinema);

            }
        }


    }

    private AfishaMerchantData cinema(AfishaCinema afishaCinema) throws Exception {
        AfishaMerchantData afishaMerchantData = new AfishaMerchantData();

        String urlCinema = afishaCinema.url;
        MerchantData merchantData = new MerchantData();
        ContactData contactData = new ContactData();

        Document documentCinema = Jsoup.connect(urlCinema).get();
        String cinemaName = documentCinema.select(".b-object-header").select("h1").text();
        merchantData.setTitle(cinemaName);
        merchantData.setName(cinemaName);//todo

        String address = documentCinema.select("div").select(".m-margin-btm").text();
        contactData.addContactParam(new ContactParam(ContactParam.ADDRESS_PARAM, address, ContactParam.Type.ADDRESS));

        //System.out.println("map url = " + afishaCinema.mapUrl);
        Document documentMapCinema = Jsoup.connect(afishaCinema.mapUrl).get();

        double latitude = Double.parseDouble(documentMapCinema.select("meta[property=og:latitude]").attr("content"));
        double longitude = Double.parseDouble(documentMapCinema.select("meta[property=og:longitude]").attr("content"));
        contactData.setLocation(longitude,latitude);

        merchantData.setContactData(contactData);
        afishaMerchantData.merchantData = merchantData;

        //System.out.println(afishaCinema.scheduleUrl);

        Elements elements = null;
        Calendar current = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        do {
            String timeUrl = afishaCinema.scheduleUrl + dateFormat.format(current.getTime()) + "/";
            //System.out.println(timeUrl);

            Document documentSchedule = Jsoup.connect(timeUrl).get();

            elements = documentSchedule.select("div").select(".object").select("tr");
            for (Element element : elements) {
                String movieUrl = element.select("div").select(".clearfix").select("a").attr("href");
                Movie movie = getMovie(movieUrl);
                //System.out.println(movie);
                OfferData offerData = createOfferData(movie,element,current);
                //System.out.println(offerData.getOfferTimes());

                afishaMerchantData.movieSetMap.put(movie,offerData);
            }

            current.add(Calendar.DAY_OF_WEEK,1);
        } while (0 < elements.size());

        return afishaMerchantData;
    }

    private OfferData createOfferData(Movie movie, Element element, Calendar date) throws ParseException {
        OfferData offerData = new OfferData();

        WeekDay.Days days = WeekDay.Days.byDate(date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Elements timeInsides = element.select(".time-inside").select("span");
        for (Element timeInside : timeInsides) {
            Date dateInside = dateFormat.parse(timeInside.text());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateInside);

            OfferTimeData offerTimeData = new OfferTimeData();
            cal.add(Calendar.MINUTE,movie.duration);
            offerTimeData.start(dateInside);
            offerTimeData.finish(cal.getTime());

            offerData.addOfferTime(days, offerTimeData);
        }
        offerData.setContactData(movie.getContactData());
        offerData.setProductType(ProductType.CINEMA);
        offerData.setPublicity(Publicity.PUBLIC);
        offerData.setEnabled(true);
        offerData.setAmount(1000L);//todo

        return offerData;
    }

    private Movie getMovie(String movieUrl) throws IOException {
        Document document = Jsoup.connect(movieUrl).get();
        String title = document.select("div").select(".b-object-header").select("h1").text();
        String tags = document.select("div").select(".b-tags").text();
        String director = document.select("span[itemprop=director]").select("span[itemprop=name]").text();
        String actors = document.select("span[itemprop=actors]").text();
        String creation = document.select("span").select(".creation").text();
        String siteUrl = document.select("#ctl00_CenterPlaceHolder_ucHead_fvCreationHead_hlCity").text();
        String description = document.select("#ctl00_CenterPlaceHolder_ucMainPageContent_pDescription").text();


        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setCode(UUID.randomUUID().toString());
        movie.setEnabled(true);
        movie.setPartial(true);
        movie.setCreatedAt(new Date());
        movie.director = director;
        movie.actors = new HashSet<>(Arrays.asList(actors.split(",")));
        String[] creationArray = creation.split(",");

        movie.creation = creationArray[0] + creationArray[1];
        String min = creationArray[creationArray.length-1].trim();
        //System.out.println("min = " + min);
        if ( null != min && min.contains("мин") ) {
            movie.duration = Integer.parseInt(min.substring(0, min.indexOf("мин")).trim());
        }
        else if ( null != min && !min.contains("мин") ) {
            movie.duration = Integer.parseInt(min.trim());
        }
        else movie.duration = 90;

        movie.siteUrl = siteUrl;
        HashSet<Tag> tagHashSet = new HashSet<>();
        for (String t : tags.split(",")) {
            Tag tt = new Tag();
            tt.setTitle(t);
            tagHashSet.add(tt);
        }
        movie.tags = tagHashSet;
        movie.description =  description;

        return movie;

    }

    public static class Movie extends ProductData {
        private String director;
        private Set<String> actors;
        private String creation;
        private String siteUrl;
        private Set<Tag> tags;
        private String description;
        private Integer duration;

        @Override
        public ContactData getContactData() {
            ContactData contact = new ContactData();
            try {
                contact.addContactParam(new ContactParam("MOVIE_DIRECTOR",director, ContactParam.Type.DESCRIPTION));
                contact.addContactParam(new ContactParam("MOVIE_ACTORS", JsonUtil.toJson(actors), ContactParam.Type.DESCRIPTION));
                contact.addContactParam(new ContactParam("MOVIE_CREATION", creation, ContactParam.Type.DESCRIPTION));
                contact.addContactParam(new ContactParam("MOVIE_SITEURL", siteUrl, ContactParam.Type.DESCRIPTION));
                contact.addContactParam(new ContactParam("MOVIE_DESCRIPTION", description, ContactParam.Type.DESCRIPTION));
                contact.addContactParam(new ContactParam("MOVIE_DURATION", duration, ContactParam.Type.DESCRIPTION));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return contact;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Movie{");
            sb.append("tile='").append(getTitle()).append('\'');
            sb.append("director='").append(director).append('\'');
            sb.append(", actors=").append(actors);
            sb.append(", creation='").append(creation).append('\'');
            sb.append(", siteUrl='").append(siteUrl).append('\'');
            sb.append(", tags=").append(tags);
            sb.append(", description='").append(description).append('\'');
            sb.append(", duration='").append(duration).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }


    public static void main(String[] args) throws Exception {
//        AfishaGrabber grabber = new AfishaGrabber();
//        grabber.fillUnderground();



        AfishaCinema afishaCinema = new AfishaCinema("http://www.afisha.ru/msk/cinema/3073/");
        System.out.println(afishaCinema.city + ":" + afishaCinema.mapUrl);
        AfishaGrabber grabber = new AfishaGrabber();
        AfishaMerchantData merchantData = grabber.cinema(afishaCinema);
        System.out.println(merchantData.merchantData);
        for (Map.Entry<Movie, OfferData> entry : merchantData.movieSetMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            for (Map.Entry<WeekDay.Days, Set<OfferTimeData>> daysSetEntry : entry.getValue().getOfferTimes().entrySet()) {
                System.out.println(daysSetEntry);
            }
            for (ContactParam contactParam : entry.getValue().getContactData().getContactParams()) {
                System.out.println(contactParam);
            }
            System.out.println("-------");
        }
//        System.out.println(merchantData.getContactData().getLat() + ":" + merchantData.getContactData().getLon());
//        for (ContactParam contactParam : merchantData.getContactData().getContactParams()) {
//            System.out.println(contactParam);
//        }

//        Movie movie = grabber.getMovie("http://www.afisha.ru/movie/221930/");
//        System.out.println(movie);
//        for (ContactParam contactParam : movie.getContactData().getContactParams()) {
//            System.out.println(contactParam);
//        }

    }
}
