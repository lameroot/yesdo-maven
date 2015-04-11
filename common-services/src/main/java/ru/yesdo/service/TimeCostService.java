package ru.yesdo.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;

import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.gis.spatial.rtree.filter.SearchFilter;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.graph.repository.OfferGraphRepository;
import ru.yesdo.model.Offer;
import ru.yesdo.model.TimeCost;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lameroot on 18.03.15.
 */
@Service
public class TimeCostService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SpatialDatabaseService spatialDatabaseService;
    @Resource
    private OfferGraphRepository offerGraphRepository;
    @Resource
    private Neo4jTemplate neo4jTemplate;

    public Relationship addTimeCost(Offer offer, TimeCost timeCost) {
        Node offerNode = neo4jTemplate.getNode(offer.getGraphId());
        if ( null == offerNode ) throw new IllegalArgumentException("Unable to find node by id: " + offer.getGraphId());

        EditableLayer timeCostLayer = spatialDatabaseService.getOrCreateEditableLayer(TimeCost.SPATIAL_LAYER_NAME);
        if ( null == timeCostLayer ) throw new IllegalArgumentException("Unable to create timecost layer");

        Geometry geometry = timeCost.toGeometry(timeCostLayer.getGeometryFactory());
        logger.debug("Will try to add geometry: {} to spatial layer: {}",geometry,TimeCost.SPATIAL_LAYER_NAME);
        System.out.println("geom =" + geometry);
        SpatialDatabaseRecord spatialDatabaseRecord = timeCostLayer.add(geometry);
        spatialDatabaseRecord.setProperty(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME,timeCost.getParamsOfRelationship().get(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME));
        Node geomNode = spatialDatabaseRecord.getGeomNode();
        return neo4jTemplate.createRelationshipBetween(offerNode,geomNode,TimeCost.TIME_COST_RELATIONSHIP_NAME,timeCost.getParamsOfRelationship());

    }

    public List<TimeCost> findBy(Calendar startDay, Calendar endDay, Double startTime, Double endTime, Long startCost, Long endCost) {
        EditableLayer timeCostLayer = spatialDatabaseService.getOrCreateEditableLayer(TimeCost.SPATIAL_LAYER_NAME);
        if ( null == timeCostLayer ) throw new IllegalArgumentException("Unable to create timecost layer");

        Geometry searchBox = TimeCost.createBox(timeCostLayer.getGeometryFactory(),startDay,endDay,startTime,endTime);
        System.out.println("search box = " + searchBox);

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
        printRecords(GeoPipeline.startIntersectSearch(timeCostLayer, searchBox)
                //.getMax(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME)
                .propertyFilter(TimeCost.TIME_COST_RELATIONSHIP_COST_PARAM_NAME,startCost)//todo: почему то не работает с фильтром
                //.range(startCost.intValue(), null != endCost ? endCost.intValue() : null)
                .toSpatialDatabaseRecordList());
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

        return null;
    }

    private void printRecords(List<SpatialDatabaseRecord> records) {
        for (SpatialDatabaseRecord record : records) {
            System.out.println(record);
            Result result = neo4jTemplate.query("match (n)<-[r:TIME_COST]-(o) where ID(n) = " + record.getGeomNode().getId() + " return o", new HashMap<>());
            System.out.println(result.single());
        }
    }

    public List<Offer> findOffers() {
        List<Offer> offers = new ArrayList<>();

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
}
