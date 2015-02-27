package ru.yesdo.model.data;

import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Polygon;
import org.springframework.data.geo.Shape;
import org.springframework.data.neo4j.repository.GeoConverter;
import ru.yesdo.model.Contact;
import ru.yesdo.model.OfferTime;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Krainov
 * Date: 27.02.2015
 * Time: 18:23
 */
public class SearchGraphCriteria {

    public static final String POINTS_NAME = "points";
    private final static String searchInBoxStartNode = "%s=node:%s(\"bbox:[%s,%s,%s,%s]\")";
    private final static String searchWithinDistanceStartNode = "%s=node:%s(\"withinDistance:[%s,%s,%s]\")";
    private final static String searchWithinWKTGeometryStartNode = "%s=node:%s(withinWKTGeometry:[%s]\")";

    private Shape shape;
    private Double priceMin;
    private Double priceMax;
    private List<OfferTimeData> offerTimeDatas = new ArrayList<>();
    private String productTitleQuery;
    private String merchantName;
    private int ratingMoreThanStars;
    private boolean ratingWithComments;

    public String buildCypherQuery() {
        String graphQuery = "";
        if ( null != shape ) {
            if ( shape instanceof WithinDistance ) {
                graphQuery = nodeByWithinDistance(POINTS_NAME, Contact.LOCATION_INDEX_NAME, (WithinDistance)shape);
            }
            else if ( shape instanceof Circle ) {
                graphQuery = nodeByCircle(POINTS_NAME, Contact.LOCATION_INDEX_NAME, (Circle)shape);
            }
            else if ( shape instanceof Polygon ) {
                graphQuery = nodeByPolygon(POINTS_NAME, Contact.LOCATION_INDEX_NAME, (Polygon)shape);
            }
            else if ( shape instanceof Box ) {
                graphQuery = nodeByBox(POINTS_NAME, Contact.LOCATION_INDEX_NAME, (Box)shape);
            }
        }
        String timeQuery = "";
        if ( !offerTimeDatas.isEmpty() ) {
            for (OfferTimeData offerTimeData : offerTimeDatas) {

            }
        }

        return graphQuery;
    }

    private static final String nodeByBox(String name, String indexName, Box box) {
        return String.format(searchInBoxStartNode,name,indexName,box.getFirst().getX(),box.getFirst().getY(),box.getSecond().getX(),box.getSecond().getY());
    }
    private static final String nodeByWithinDistance(String name, String indexName, WithinDistance withinDistance) {
        return String.format(searchWithinDistanceStartNode, name, indexName, withinDistance.getLat(), withinDistance.getLon(), withinDistance.getDistanceKm());
    }
    private static final String nodeByCircle(String name, String indexName, Circle circle) {
        return String.format(searchWithinDistanceStartNode, name, indexName, circle.getCenter().getY(), circle.getCenter().getX(), circle.getRadius().getValue());
    }
    private static final String nodeByPolygon(String name, String indexName, Polygon polygon) {
        return String.format(searchWithinWKTGeometryStartNode, name, indexName, GeoConverter.toWkt(polygon));
    }

}
