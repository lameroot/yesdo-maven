package ru.yesdo.service;

import org.neo4j.cypherdsl.grammar.Execute;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import ru.yesdo.model.Contact;
import ru.yesdo.model.Offer;

import static org.neo4j.cypherdsl.CypherQuery.*;
import static org.neo4j.cypherdsl.querydsl.CypherQueryDSL.*;
import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * User: Krainov
 * Date: 27.02.2015
 * Time: 16:43
 */
public class OfferService {

    private final static String searchInBoxStartNode = "%s=node:%s(\"bbox:[%s,%s,%s,%s]\")";
    private final static String searchWithinDistanceStartNode = "%s=node:%s(\"withinDistance:[%s,%s,%s]\")";

    private static final String nodeByBox(String name, String indexName, Box box) {
        return String.format(searchInBoxStartNode,name,indexName,box.getFirst().getX(),box.getFirst().getY(),box.getSecond().getX(),box.getSecond().getY());
    }
    private static final String nodeByWithinDistance(String name, String indexName, final double lat, double lon, double distanceKm) {
        return String.format(searchWithinDistanceStartNode, name, indexName, lat, lon, distanceKm);
    }
    private static final String nodeByWithinDistance(String name, String indexName, Circle circle) {
        return String.format(searchWithinDistanceStartNode, name, indexName, circle.getCenter().getY(), circle.getCenter().getX(), circle.getRadius().getValue());
    }

    public void findOfferByCriteria() {




    }

    public static void main(String[] args) {
        OfferService offerService = new OfferService();
        offerService.findOfferByCriteria();

        Box box = new Box(new Point(10.0,20.0),new Point(30.0,40.0));
        System.out.println(OfferService.nodeByBox("points",Contact.LOCATION_INDEX_NAME,box));

        System.out.println(OfferService.nodeByWithinDistance("points", Contact.LOCATION_INDEX_NAME, 10.0, 20.0, 30.0));
        System.out.println(OfferService.nodeByWithinDistance("points", Contact.LOCATION_INDEX_NAME, new Circle(10.0,20.0,30.0)));
    }
}
