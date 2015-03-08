package ru.yesdo.service.grabber.kudago;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lameroot on 01.03.15.
 */
public class KudaGoGrabber {

    private static final Logger logger = LoggerFactory.getLogger(KudaGoGrabber.class);
    private String url = "http://kudago.com/";
    private String city = "msk";
    private String page = "?page=";


    public KudaGoGrabber() {
    }

    public KudaGoGrabber(String url, String city) {
        this.url = url;
        this.city = city;
    }

    private int getCountPages(Document document) throws IOException {
        int count = 1;


        Elements elements = document.select(".pagination");
        String countAsString = elements.attr("data-pages");

        return null != countAsString ? Integer.parseInt(countAsString) : count;
    }

    private Elements getAllObjectsOnPage(Document document) {
        Elements elements = document.select(".feed").select(".post");
        return elements;

    }

    private void getOneElement(Element element) {

    }

    public void grabCinemas() throws IOException {
        String uri = url + city + "/cinemas/";
        logger.debug("Try to get document by url: {}",uri);
        Document document = Jsoup.connect(uri).get();

        int countPages = getCountPages(document);
        for (int i = 1; i <= countPages; i++) {
            String fullUrl = uri + page + i;
            System.out.println(fullUrl);
            Document pageDocument = Jsoup.connect(fullUrl).get();
            Elements allElementsOnPage = getAllObjectsOnPage(pageDocument);
            for (Element element : allElementsOnPage) {

            }
        }

    }

    public static void main(String[] args) throws IOException {
        KudaGoGrabber grabber = new KudaGoGrabber();
        //grabber.grabCinemas();

        Document pageDocument = Jsoup.connect("http://kudago.com/msk/cinemas/?page=1").get();
        Elements elements = pageDocument.select(".feed").select(".post");
        System.out.println(elements);
    }

}
