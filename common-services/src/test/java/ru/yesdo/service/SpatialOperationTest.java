package ru.yesdo.service;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.GeneralCommonServiceDbTest;

import java.util.List;

/**
 * User: Krainov
 * Date: 20.02.2015
 * Time: 17:07
 * http://neo4j-contrib.github.io/spatial/
 */
public class SpatialOperationTest extends GeneralCommonServiceDbTest {

    @Test
    @Transactional
    public void testCreateCoordinate() {
        System.out.println("cleant");
//        SimplePointLayer layer = spatialDatabaseService.createSimplePointLayer("geom");
//        Coordinate coordinate1 = new Coordinate(10.0,10.0);
//        Coordinate coordinate2 = new Coordinate(20.0,20.0);
//        Coordinate coordinate3 = new Coordinate(30.0,30.0);
//
//        layer.add(coordinate1);
//        layer.add(coordinate2);
//        layer.add(coordinate3);
//
//        Coordinate myCoordinate = new Coordinate(15.0,15.0);
//
//
//        List<GeoPipeFlow> pipeFlows = layer.findClosestPointsTo(myCoordinate,0.000010);
//        for (GeoPipeFlow pipeFlow : pipeFlows) {
//            SpatialDatabaseRecord record = pipeFlow.getRecord();
//            System.out.println(record);
//        }
    }

}
