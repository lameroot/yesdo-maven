package ru.yesdo.service;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.filter.text.cql2.CQLException;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.AbstractFilterGeoPipe;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.gis.spatial.pipes.impl.FilterPipe;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.mapping.MappingPolicy;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.stereotype.Service;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.Offer;
import ru.yesdo.model.Product;
import ru.yesdo.model.TimeCost;
import ru.yesdo.model.data.OfferIdentificator;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by lameroot on 18.03.15.
 */
@Service
public class TimeCostService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String QUERY_FIND_OFFER = "match (n)<-[r:TIME_COST]-(o) where ID(n) = %d return o";
    private static final String OFFER_GRAPH_ID_PARAM_NAME = "offer_id";
    private static final String PRODUCT_GRAPH_ID_PARAM_NAME = "product_id";
    private static final String MERCHANT_GRAPH_ID_PARAM_NAME = "merchant_id";


    @Resource
    private SpatialDatabaseService spatialDatabaseService;
    @Resource
    private Neo4jTemplate neo4jTemplate;

    public Relationship addTimeCost(Offer offer, TimeCost timeCost) {
        Node offerNode = neo4jTemplate.getNode(offer.getGraphId());
        if ( null == offerNode ) throw new IllegalArgumentException("Unable to find node by id: " + offer.getGraphId());
        timeCost.isValid(true);

        EditableLayer timeCostLayer = spatialDatabaseService.getOrCreateEditableLayer(TimeCost.SPATIAL_LAYER_NAME);
        if ( null == timeCostLayer ) throw new IllegalArgumentException("Unable to create timecost layer");

        Geometry geometry = timeCost.toGeometry(timeCostLayer.getGeometryFactory());
        logger.debug("Will try to add geometry: {} to spatial layer: {}",geometry, TimeCost.SPATIAL_LAYER_NAME);
        SpatialDatabaseRecord spatialDatabaseRecord = timeCostLayer.add(geometry);
        spatialDatabaseRecord.setProperty(OFFER_GRAPH_ID_PARAM_NAME,offer.getGraphId());
        spatialDatabaseRecord.setProperty(PRODUCT_GRAPH_ID_PARAM_NAME,offer.getProduct().getGraphId());
        spatialDatabaseRecord.setProperty(MERCHANT_GRAPH_ID_PARAM_NAME,offer.getMerchant().getGraphId());
        spatialDatabaseRecord.setProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME,timeCost.getParamsOfRelationship().get(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME));
        Node geomNode = spatialDatabaseRecord.getGeomNode();
        return neo4jTemplate.createRelationshipBetween(offerNode,geomNode, TimeCost.TIME_COST_RELATIONSHIP_NAME,timeCost.getParamsOfRelationship());

    }

    public Set<OfferIdentificator> findOfferIdsBy(Calendar startDay, Calendar endDay, Double startTime, Double endTime, Long startCost, Long endCost) throws CQLException {
        EditableLayer timeCostLayer = spatialDatabaseService.getOrCreateEditableLayer(TimeCost.SPATIAL_LAYER_NAME);
        if ( null == timeCostLayer ) throw new IllegalArgumentException("Unable to create timecost layer");

        Geometry searchBox = TimeCost.createBox(timeCostLayer.getGeometryFactory(), startDay, endDay, startTime, endTime);
        logger.debug("search box = " + searchBox);

        GeoPipeline pipe = GeoPipeline.startIntersectSearch(timeCostLayer, searchBox);
        GeoPipeline result = pipe
                .addPipe(new FilterRecordProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME, startCost, FilterPipe.Filter.GREATER_THAN_EQUAL))
                .addPipe(new FilterRecordProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME, endCost, FilterPipe.Filter.LESS_THAN_EQUAL))
                .copyDatabaseRecordProperties(new String[]{OFFER_GRAPH_ID_PARAM_NAME,PRODUCT_GRAPH_ID_PARAM_NAME,MERCHANT_GRAPH_ID_PARAM_NAME,TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME});

        Set<OfferIdentificator> ids = new HashSet<>();
        for (GeoPipeFlow flow : result) {
            Long offerId = (Long)flow.getProperty(OFFER_GRAPH_ID_PARAM_NAME);
            Long productId = (Long)flow.getProperty(PRODUCT_GRAPH_ID_PARAM_NAME);
            Long merchantId = (Long)flow.getProperty(MERCHANT_GRAPH_ID_PARAM_NAME);

            if ( null != offerId && !ids.contains(offerId) ) {
                ids.add(new OfferIdentificator(offerId,productId,merchantId));
            }
        }

        return ids;
    }

    public Set<Offer> findOffersBy(Calendar startDay, Calendar endDay, Double startTime, Double endTime, Long startCost, Long endCost) throws CQLException {
        Set<Offer> offers = new HashSet<>();
        Set<OfferIdentificator> ids = findOfferIdsBy(startDay, endDay, startTime, endTime, startCost, endCost);
        for (OfferIdentificator id : ids) {
            if ( null != id ) {
                Offer offer = idToOffer(id);
                offers.add(offer);
            }
        }
        return offers;
    }

    private List<SpatialDatabaseRecord> findBy(Calendar startDay, Calendar endDay, Double startTime, Double endTime, Long startCost, Long endCost) throws CQLException {
        EditableLayer timeCostLayer = spatialDatabaseService.getOrCreateEditableLayer(TimeCost.SPATIAL_LAYER_NAME);
        if ( null == timeCostLayer ) throw new IllegalArgumentException("Unable to create timecost layer");

        Geometry searchBox = TimeCost.createBox(timeCostLayer.getGeometryFactory(), startDay, endDay, startTime, endTime);
        logger.debug("search box = " + searchBox);

        GeoPipeline pipe = GeoPipeline.startIntersectSearch(timeCostLayer, searchBox);
        return pipe.addPipe(new FilterRecordProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME, startCost, FilterPipe.Filter.GREATER_THAN_EQUAL))
                .addPipe(new FilterRecordProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME, endCost, FilterPipe.Filter.LESS_THAN_EQUAL))
                .toSpatialDatabaseRecordList();
    }

    private Offer idToOffer(OfferIdentificator offerIdentificator) {
        Offer offer = new Offer();
        offer.setGraphId(offerIdentificator.getOfferId());
        if ( null != offerIdentificator.getProductId() ) {

            Result result = neo4jTemplate.query("start n=node({nodeId}) return n.db_id,n.title,n.code", MapUtil.map("nodeId", offerIdentificator.getProductId()));
            Map<String,Object> map = (Map)result.singleOrNull();

            Product product = new Product();
            product.setGraphId(offerIdentificator.getProductId());
            if ( null != map && !map.isEmpty() ) {
                product.setId((Long) map.get("n.db_id"));
                product.setTitle((String) map.get("n.title"));
                product.setCode((String) map.get("n.code"));
            }

            offer.setProduct(product);
        }
        if ( null != offerIdentificator.getMerchantId() ) {
            Merchant merchant = neo4jTemplate.createEntityFromStoredType(neo4jTemplate.getNode(offerIdentificator.getMerchantId()), MappingPolicy.MAP_FIELD_DIRECT_POLICY);
            offer.setMerchant(merchant);
        }

        return offer;
    }

    private Set<Offer> toOffers(List<SpatialDatabaseRecord> records) {
        Set<Offer> offers = new HashSet<>();
        Set<Long> ids = new HashSet<>();
        for (SpatialDatabaseRecord record : records) {
            Result result = neo4jTemplate.query(String.format(QUERY_FIND_OFFER,record.getGeomNode().getId()), new HashMap<>());
            Map o = (Map)result.singleOrNull();
            if ( null != o) {
                Node node = (Node) o.get("o");
                if ( null != node && !ids.contains(node.getId()) ) {
                    offers.add(neo4jTemplate.findOne(node.getId(), Offer.class));
                    ids.add(node.getId());
                }
            }
        }
        ids.clear();
        ids = null;
        return offers;
    }


    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    /*
    public TimeCost create(Offer offer, TimeCost timeCost) {
        //if ( null == (offer = offerGraphRepository.findOne(offer.getGraphId())) ) throw new IllegalArgumentException("Offer with graphId: " + offer.getGraphId() + " doesnt exist");
        EditableLayer timeCostLayer = spatialDatabaseService.getOrCreateEditableLayer("timecost");
        System.out.println("timeCostLayer = " + timeCostLayer);
        if ( null == timeCostLayer ) throw new IllegalArgumentException("Unable to create timecost layer");

        Coordinate coordinate = new Coordinate(timeCost.getX(),timeCost.getY(),timeCost.getZ());
        Coordinate coordinate2 = new Coordinate(timeCost.getX()+10,timeCost.getY()+10,timeCost.getZ()+1);
        Coordinate coordinate3 = new Coordinate(timeCost.getX()+5,timeCost.getY()+5,timeCost.getZ());
        Coordinate coordinate4 = new Coordinate(timeCost.getX()+150,timeCost.getY()+150,timeCost.getZ());
        Geometry geometry = timeCostLayer.getGeometryFactory().createPoint(coordinate);
        Geometry geometry2 = timeCostLayer.getGeometryFactory().createPoint(coordinate2);
        Geometry line = timeCostLayer.getGeometryFactory().createLineString(new Coordinate[]{coordinate,coordinate2});
        Geometry line2 = timeCostLayer.getGeometryFactory().createLineString(new Coordinate[]{coordinate,coordinate2});

        System.out.println("geometry = " + geometry);
        System.out.println("geometry2 = " + geometry2);
        System.out.println("line = " + line);
        SpatialDatabaseRecord spatialDatabaseRecord = timeCostLayer.add(geometry);
        SpatialDatabaseRecord spatialDatabaseRecord2 = timeCostLayer.add(geometry2);
        SpatialDatabaseRecord spatialDatabaseRecord3 = timeCostLayer.add(line);
        SpatialDatabaseRecord spatialDatabaseRecord4 = timeCostLayer.add(line2);
        System.out.println("database record = " + spatialDatabaseRecord);
        System.out.println("database record2 = " + spatialDatabaseRecord2);
        System.out.println("database record3 = " + spatialDatabaseRecord3);
        System.out.println("database record4 = " + spatialDatabaseRecord4);

        Node from = neo4jTemplate.getNode(offer.getGraphId());
        System.out.println("from = " + from);
        if ( null == from ) throw new IllegalArgumentException("Unable to find node by id: " + offer.getGraphId());
        Node to = spatialDatabaseRecord.getGeomNode();
        Node to2 = spatialDatabaseRecord2.getGeomNode();
        Node to3 = spatialDatabaseRecord3.getGeomNode();
        Node to4 = spatialDatabaseRecord4.getGeomNode();
        System.out.println("to = " + to);
        System.out.println("to2 = " + to2);
        System.out.println("to3 = " + to3);
        System.out.println("to4 = " + to4);

        Relationship relationship = neo4jTemplate.createRelationshipBetween(from, to, "TIME_COST", new HashMap<>());
        Relationship relationship2 = neo4jTemplate.createRelationshipBetween(from, to2, "TIME_COST", new HashMap<>());
        Relationship relationship3 = neo4jTemplate.createRelationshipBetween(from, to3, "TIME_COST", new HashMap<>());
        Relationship relationship4 = neo4jTemplate.createRelationshipBetween(from, to4, "TIME_COST", new HashMap<>());
        System.out.println("realation ship = " + relationship);
        if ( null == relationship ) throw new IllegalArgumentException("Unable to create relationship between: " + from + " and " + to + " nodes");
        System.out.println("realation ship2 = " + relationship2);
        System.out.println("real ship3 = " + relationship3);
        System.out.println("real ship4 = " + relationship4);

        return timeCost;
    }
    */

    private static class FilterRecordProperty extends AbstractFilterGeoPipe {
        private String key;
        private Object value;
        private FilterPipe.Filter comparison;

        public FilterRecordProperty(String key, Object value) {
            this(key, value, FilterPipe.Filter.EQUAL);
        }

        public FilterRecordProperty(String key, Object value, FilterPipe.Filter comparison) {
            this.key = key;
            this.value = value;
            this.comparison = comparison;
        }

        @Override
        protected boolean validate(GeoPipeFlow flow) {
            Object prop = flow.getRecord().getProperty(key);
            if ( null == prop ) return value == null;
            switch (comparison) {
                case EQUAL: {
                    return prop.equals(value);
                }
                case NOT_EQUAL: {
                    return !prop.equals(value);
                }
                case GREATER_THAN: {
                    if ( null != value ) {
                        return ((Comparable)prop).compareTo(value) == 1;
                    }
                    return false;
                }
                case LESS_THAN: {
                    if ( null != value ) {
                        return ((Comparable)prop).compareTo(value) == -1;
                    }
                    return false;
                }
                case GREATER_THAN_EQUAL: {
                    if ( null != value ) {
                        return ((Comparable)prop).compareTo(value) >= 0;
                    }
                    return false;
                }
                case LESS_THAN_EQUAL: {
                    if ( null != value ) {
                        return ((Comparable)prop).compareTo(value) <= 0;
                    }
                    return false;
                }
                default:
                    throw new IllegalArgumentException("Invalid state as no valid filter was provided");
            }
        }
    }

    /*
    Coordinate coordinate = new Coordinate(TimeCost.toDay(startDay),startTime);
        System.out.println("startContainSearch");
        printRecords(GeoPipeline.startContainSearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());
        System.out.println("startCoveredBySearch");
        printRecords(GeoPipeline.startCoveredBySearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());
        System.out.println("startCoverSearch");
        printRecords(GeoPipeline.startCoverSearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());
        System.out.println("startCrossSearch");
        printRecords(GeoPipeline.startCrossSearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());
        System.out.println("startIntersectSearch");
        GeoPipeline pipe = GeoPipeline.startIntersectSearch(timeCostLayer, searchBox);
//        List<GeoPipeFlow> geoPipeFlows =
//                pipe.addPipe(new FilterRecordProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME,startCost)).toList();

        System.out.println("startOverlapSearch");
        printRecords(GeoPipeline.startOverlapSearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());
        System.out.println("startTouchSearch");
        printRecords(GeoPipeline.startTouchSearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());
        System.out.println("startWithinSearch");
        printRecords(GeoPipeline.startWithinSearch(timeCostLayer, searchBox).toSpatialDatabaseRecordList());

        List<Node> records= GeoPipeline.startContainSearch(timeCostLayer, searchBox).toNodeList();
        //List<Node> records = GeoPipeline.startContainSearch(timeCostLayer, searchBox).toNodeList();
        for (Node record : records) {
            System.out.println(record);



        }
     */

}
