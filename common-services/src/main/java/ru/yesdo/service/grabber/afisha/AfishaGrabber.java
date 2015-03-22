package ru.yesdo.service.grabber.afisha;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.LongRange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.model.*;
import ru.yesdo.model.data.*;
import ru.yesdo.service.GeoDataImporter;
import ru.yesdo.service.grabber.AbstractGrabber;
import ru.yesdo.service.grabber.Grabber;

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
public class AfishaGrabber extends AbstractGrabber {

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
//        for (Map.Entry<String, AfishaUndergroundCinemaList> entry : undergroundCinemaListMap.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue().getGeoData());
//        }
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

    private Cinema getCinema(String urlCinema) throws Exception {
        Cinema cinema = new Cinema(urlCinema);

        Document documentCinema = Jsoup.connect(urlCinema).get();
        String cinemaName = documentCinema.select(".b-object-header").select("h1").text();
        cinema.setTitle(cinemaName);
        cinema.setName(cinemaName);//todo: unique

        ContactData contactData = new ContactData();
        String address = documentCinema.select("div").select(".m-margin-btm").text();
        if ( null != address ) {
            String[] arrayOfPrices = StringUtils.substringsBetween(address, "Билеты", "р")[0].trim().split("–");
            long startPrice = Long.parseLong(arrayOfPrices[0]) * 100;
            long finishPrice = Long.parseLong(arrayOfPrices[1]) * 100;

            cinema.startPrice = startPrice;
            cinema.finishPrice = finishPrice;

            contactData.addContactParam(new ContactParam(ContactParam.PRICE_MIN_PARAM, startPrice, ContactParam.Type.DESCRIPTION));
            contactData.addContactParam(new ContactParam(ContactParam.PRICE_MAX_PARAM, finishPrice, ContactParam.Type.DESCRIPTION));
        }

        contactData.addContactParam(new ContactParam(ContactParam.ADDRESS_PARAM, address, ContactParam.Type.ADDRESS));

        Document documentMapCinema = Jsoup.connect(cinema.mapUrl).get();

        double latitude = Double.parseDouble(documentMapCinema.select("meta[property=og:latitude]").attr("content"));
        double longitude = Double.parseDouble(documentMapCinema.select("meta[property=og:longitude]").attr("content"));
        contactData.setLocation(longitude,latitude);

        cinema.setContactData(contactData);

        return cinema;
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
        String[] arrayOfPrices = StringUtils.substringsBetween(address,"Билеты","р")[0].trim().split("–");
        long startPrice = Long.parseLong(arrayOfPrices[0]) * 100;
        long endPrices = Long.parseLong(arrayOfPrices[1]) * 100;

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
                movie.intervalPrices = new LongRange(startPrice,endPrices);
                //System.out.println(movie);
                Set<OfferData> offerDatas = createOfferData(movie,element,current);
                //System.out.println(offerData.getOfferTimes());

                afishaMerchantData.movieSetMap.put(movie,offerDatas);
            }

            current.add(Calendar.DAY_OF_WEEK,1);
        } while (0 < elements.size());

        return afishaMerchantData;
    }

    private Set<OfferData> createOfferData(Movie movie, Element element, Calendar date) throws ParseException {
        Set<OfferData> offerDatas = new HashSet<>();



        WeekDay.Days days = WeekDay.Days.byDate(date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Elements timeInsides = element.select(".time-inside").select("span");
        for (Element timeInside : timeInsides) {
            OfferData offerData = new OfferData();

            Date dateInside = dateFormat.parse(timeInside.text());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateInside);

            long amount = 0L;
            LongRange intervalPrices = movie.intervalPrices;
            if ( null != intervalPrices ) {
                Long min = intervalPrices.getMinimumLong();
                Long max = intervalPrices.getMaximumLong();

                int hours = cal.get(Calendar.HOUR_OF_DAY);
                if ( hours < 12 ) amount = min;
                else if ( 12 <= hours && hours < 17 ) amount = (max - min) /2;
                else amount = max;
            }

            cal.add(Calendar.MINUTE,movie.duration);

            TimeCost timeCost = TimeCost.duringOneDay(date,
                    TimeCost.createTime(dateInside.getHours(),dateInside.getMinutes()),
                    TimeCost.createTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE)),
                    amount);
            offerData.addTimeCost(timeCost);

//            OfferTimeData offerTimeData = new OfferTimeData();
//            offerTimeData.start(dateInside);
//            offerTimeData.finish(cal.getTime());
//
//            offerData.addOfferTime(days, offerTimeData);

            offerData.setContactData(movie.getContactData());
            offerData.setProductType(ProductType.CINEMA);
            offerData.setPublicity(Publicity.PUBLIC);
            offerData.setEnabled(true);
            offerData.setAmount(amount);

            offerDatas.add(offerData);
        }


        return offerDatas;
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
        movie.setCode(Movie.getId(movieUrl));
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




    public static void main(String[] args) throws Exception {
//        AfishaGrabber grabber = new AfishaGrabber();
//        grabber.fillUnderground();



        AfishaCinema afishaCinema = new AfishaCinema("http://www.afisha.ru/msk/cinema/3073/");
        System.out.println(afishaCinema.city + ":" + afishaCinema.mapUrl);
        AfishaGrabber grabber = new AfishaGrabber();
        AfishaMerchantData merchantData = grabber.cinema(afishaCinema);
        System.out.println(merchantData.merchantData);
        for (Map.Entry<Movie, Set<OfferData>> entry : merchantData.movieSetMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            System.out.println("-------");
            for (OfferData offerData : entry.getValue()) {
                System.out.println(offerData);
                for (Map.Entry<WeekDay.Days, Set<OfferTimeData>> daysSetEntry : offerData.getOfferTimes().entrySet()) {
                    System.out.println(daysSetEntry.getKey() + ":" + daysSetEntry.getValue());
                }
                for (ContactParam contactParam : offerData.getContactData().getContactParams()) {
                    System.out.println(contactParam);
                }
            }
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

    private static final String AFISHA_URL_PARAM = "afisha_url_param";
    private static final String AFISHA_PAGE_PARAM = "afisha_page_param";

    @Override
    public Set<Activity> grabActivities() {
        Set<Activity> activities = new HashSet<>();

        ActivityData entertainmentData = new ActivityData("entertainment");
        entertainmentData.setTitle("Равзлечения");
        Activity entertainment = activityService.create(entertainmentData);
        if ( null == entertainment ) throw new IllegalArgumentException("Unable to create activity: entertainment");
        activities.add(entertainment);

        ActivityData cinemaData = new ActivityData("cinema");
        cinemaData.setTitle("Кино");
        cinemaData.addParent(entertainment);
        //cinemaData.addParam(AFISHA_URL_PARAM,"http://www.afisha.ru/msk/cinema/");
        Activity cinema = activityService.create(cinemaData);
        if ( null == cinema ) throw new IllegalArgumentException("Unable to create activity: cinema");
        activities.add(cinema);

        ActivityData cinemasData = new ActivityData("cinemas");
        cinemasData.setTitle("Кинотеатры");
        cinemasData.addParent(cinema);
        cinemasData.addParam(AFISHA_URL_PARAM, "http://www.afisha.ru/msk/cinemas/cinema_list/");
        cinemasData.addParam(AFISHA_PAGE_PARAM, "page%d");
        Activity cinemas = activityService.create(cinemasData);
        if ( null == cinemas ) throw new IllegalArgumentException("Unable to create activity: cinemas");
        activities.add(cinemas);

        return activities;

    }

    @Override
    public Set<Merchant> grabMerchants(Integer count) throws Exception {
        Set<Merchant> merchants = new HashSet<>();
        Page<Activity> page = activityService.findAll(new PageRequest(0, 100));
        for (Activity activity : page) {
            String url = null;
            if ( null == (url = (String)activity.getParams().getProperty(AFISHA_URL_PARAM)) ) continue;
            String pageParam = (String)activity.getParams().getProperty(AFISHA_PAGE_PARAM);
            int pageNum = 1;
            String urlConnect = url;
            if ( null != pageParam ) urlConnect = url + String.format(pageParam,pageNum);
            Elements objectList = null;
            List<String> targetUrls = new ArrayList<>();
            do {
                System.out.println("url : " + urlConnect);
                Document document = Jsoup.connect(urlConnect).get();
                objectList = document.select("#objects-list").select("div").select(".places-list-item").select("a[id]");

                for (Element listItem : objectList) {
                    if ( null != count && count <= targetUrls.size() ) break;
                    String href = listItem.attr("href");
                    targetUrls.add(href);
                }
                if ( null != count && count <= targetUrls.size() ) break;
                pageNum++;
                urlConnect = url + String.format(pageParam,pageNum);

            } while ( 0 < objectList.size() );

            System.out.println("count merchants href = " + targetUrls.size());
            List<Cinema> cinemas = new ArrayList<>();
            for (String targetUrl : targetUrls) {
                Cinema cinema = getCinema(targetUrl);
                cinemas.add(cinema);
            }
            System.out.println("count cinemas = " + cinemas.size());
            for (Cinema cinema : cinemas) {
                cinema.addActivity(activity);
                ContactData contactData = cinema.getContactData();
                contactData.addContactParam(new ContactParam(AFISHA_URL_PARAM,cinema.scheduleUrl, ContactParam.Type.ADDRESS));
                contactData.addContactParam(new ContactParam(ContactParam.PRICE_MIN_PARAM,cinema.startPrice, ContactParam.Type.DESCRIPTION));
                contactData.addContactParam(new ContactParam(ContactParam.PRICE_MAX_PARAM,cinema.finishPrice, ContactParam.Type.DESCRIPTION));

                Merchant merchant = merchantService.create(cinema);
                if ( null == merchant ) throw new IllegalArgumentException("Unable to create merchant: " + cinema);
                merchants.add(merchant);
            }
        }
        return merchants;
    }

    @Override
    @Transactional
    public void grabProductAndOffers(boolean onlyProduct) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        for (Merchant merchant : merchantGraphRepository.findAll(new PageRequest(0, 100))) {
            if ( null == merchant.getContact() ) continue;
            neo4jTemplate.fetch(merchant.getContact());
            if ( null != merchant.getContact().getContactParam(AFISHA_URL_PARAM) ) {
                String scheduleUrl = (String)merchant.getContact().getContactParam(AFISHA_URL_PARAM).getValue();
                long startPrice = 0L;
                if ( null != merchant.getContact().getContactParam(ContactParam.PRICE_MIN_PARAM) )
                    startPrice = (Integer)merchant.getContact().getContactParam(ContactParam.PRICE_MAX_PARAM).getValue();
                long finishPrice = 10000L;
                if ( null != merchant.getContact().getContactParam(ContactParam.PRICE_MAX_PARAM))
                    finishPrice = (Integer)merchant.getContact().getContactParam(ContactParam.PRICE_MAX_PARAM).getValue();

                Elements elements = null;
                Calendar current = Calendar.getInstance();

                Set<Product> products = new HashSet<>();
                do {
                    String timeUrl = scheduleUrl + dateFormat.format(current.getTime()) + "/";
                    System.out.println("timeUrl : " + timeUrl);

                    Document documentSchedule = Jsoup.connect(timeUrl).get();
                    elements = documentSchedule.select("div").select(".object").select("tr");

                    Set<Product> productsOnTime = new HashSet<>();
                    for (Element element : elements) {
                        String movieUrl = element.select("div").select(".clearfix").select("a").attr("href");
                        Movie movie = getMovie(movieUrl);
                        movie.setMerchant(merchant);
                        try {
                            Product product = productService.getOrCreate(movie);
                            if (null == product) throw new IllegalArgumentException("Unable to create product");

                            if (!onlyProduct) {
                                movie.intervalPrices = new LongRange(startPrice, finishPrice);
                                Set<OfferData> offerDatas = createOfferData(movie, element, current);
                                for (OfferData offerData : offerDatas) {
                                    Offer offer = merchantService.concludeOffer(merchant, product, offerData);
                                    if (null == offer) throw new IllegalArgumentException("Unable to create offer");
                                }
                            }
                            productsOnTime.add(product);
                        } catch (AlreadyExistException e) {
                            continue;
                        }

                    }
                    System.out.println("On " + current.getTime() + " we have : " + productsOnTime.size() + " products");
                    products.addAll(productsOnTime);

                    current.add(Calendar.DAY_OF_WEEK,1);
                } while (0 < elements.size());

                System.out.println("For merchant: " + merchant + " we have " + products.size() + " products");

            }
        }
    }
}
