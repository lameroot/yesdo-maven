package ru.yesdo.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.junit.Test;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.GeneralCommonServiceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

/**
 * User: Krainov
 * Date: 20.02.2015
 * Time: 17:07
 * http://neo4j-contrib.github.io/spatial/
 */
public class SpatialOperationTest extends GeneralCommonServiceTest {

    @Test()
    @Transactional
    @Rollback(false)
    public void testCreateCoordinate() {
        System.out.println("cleant");

        EditableLayer layer = spatialDatabaseService.getOrCreateEditableLayer("geom");
        Coordinate[] coordinates = new Coordinate[] { new Coordinate(0, 0,0), new Coordinate(0, 1,3) };
        Geometry geometry = layer.getGeometryFactory().createLineString(coordinates);
        layer.add(geometry);
        debugLRS(geometry);

        /*
        SimplePointLayer layer = spatialDatabaseService.createSimplePointLayer("geom");
        Coordinate coordinate1 = new Coordinate(10.0,10.0,10.0);
        Coordinate coordinate2 = new Coordinate(20.0,20.0,20.0);
        Coordinate coordinate3 = new Coordinate(30.0,30.0,30.0);

        layer.add(coordinate1);
        layer.add(coordinate2);
        layer.add(coordinate3);

        Coordinate myCoordinate = new Coordinate(15.0,15.0,15.0);


        List<GeoPipeFlow> pipeFlows = layer.findClosestPointsTo(myCoordinate, 0.000010);
        for (GeoPipeFlow pipeFlow : pipeFlows) {
            SpatialDatabaseRecord record = pipeFlow.getRecord();
            System.out.println(record);
        }

        for (Geometry geometry : layer.getAllGeometries()) {
            System.out.println(geometry);
        }
        */
    }

    private void debugLRS(Geometry geometry) {
        LengthIndexedLine line = new com.vividsolutions.jts.linearref.LengthIndexedLine(geometry);
        double length = line.getEndIndex() - line.getStartIndex();
        System.out.println("Have Geometry: " + geometry);
        System.out.println("Have LengthIndexedLine: " + line);
        System.out.println("Have start index: " + line.getStartIndex());
        System.out.println("Have end index: " + line.getEndIndex());
        System.out.println("Have length: " + length);
        System.out.println("Extracting point at position 0.0: " + line.extractPoint(0.0));
        System.out.println("Extracting point at position 0.1: " + line.extractPoint(0.1));
        System.out.println("Extracting point at position 0.5: " + line.extractPoint(0.5));
        System.out.println("Extracting point at position 0.9: " + line.extractPoint(0.9));
        System.out.println("Extracting point at position 1.0: " + line.extractPoint(1.0));
        System.out.println("Extracting point at position 1.5: " + line.extractPoint(1.5));
        System.out.println("Extracting point at position 1.5 offset 0.5: " + line.extractPoint(1.5, 0.5));
        System.out.println("Extracting point at position 1.5 offset -0.5: " + line.extractPoint(1.5, -0.5));
        System.out.println("Extracting point at position " + length + ": " + line.extractPoint(length));
        System.out.println("Extracting point at position " + (length / 2) + ": " + line.extractPoint(length / 2));
        System.out.println("Extracting line from position 0.1 to 0.2: " + line.extractLine(0.1, 0.2));
        System.out.println("Extracting line from position 0.0 to " + (length / 2) + ": " + line.extractLine(0, length / 2));
        LocationIndexedLine pline = new LocationIndexedLine(geometry);
        System.out.println("Have LocationIndexedLine: " + pline);
        System.out.println("Have start index: " + pline.getStartIndex());
        System.out.println("Have end index: " + pline.getEndIndex());
        System.out.println("Extracting point at start: " + pline.extractPoint(pline.getStartIndex()));
        System.out.println("Extracting point at end: " + pline.extractPoint(pline.getEndIndex()));
        System.out.println("Extracting point at start offset 0.5: " + pline.extractPoint(pline.getStartIndex(), 0.5));
        System.out.println("Extracting point at end offset 0.5: " + pline.extractPoint(pline.getEndIndex(), 0.5));
    }

    @Test
    public void test3d() {
        EditableLayer layer = spatialDatabaseService.getOrCreateEditableLayer("test");
        Coordinate[] coordinates = new Coordinate[] { new Coordinate(1,1,1), new Coordinate(2,2,2) };
        Geometry geometry = layer.getGeometryFactory().createLineString(coordinates);
        layer.add(geometry);
        debugLRS(geometry);

        Geometry point = layer.getGeometryFactory().createPoint(new Coordinate(1.5,1.5,1.5));
        double d = point.distance(geometry);
        System.out.println("d = " + d);
        System.out.println(geometry.contains(point));
        System.out.println(geometry.contains(layer.getGeometryFactory().createPoint(new Coordinate(1,1,1))));

//        Geometry box = layer.getGeometryFactory().
        Envelope envelope = new ReferencedEnvelope3D();
    }


    @Test
    @Transactional
    @Rollback(false)
    public void testSearchClosestWithShortLongLineStrings() throws ParseException {

        EditableLayer layer = spatialDatabaseService.getOrCreateEditableLayer("test", "WKT");
        WKTReader wkt = new WKTReader(layer.getGeometryFactory());
        Geometry shortLineString = wkt.read("LINESTRING(16.3493032 48.199882,16.3479487 48.1997337)");
        Geometry longLineString = wkt
                .read("LINESTRING(16.3178388 48.1979135,16.3195494 48.1978011,16.3220815 48.197824,16.3259696 48.1978297,16.3281211 48.1975952,16.3312482 48.1968743,16.3327931 48.1965196,16.3354641 48.1959911,16.3384376 48.1959609,16.3395792 48.1960223,16.3458708 48.1970974,16.3477719 48.1975147,16.348008 48.1975665,16.3505572 48.1984533,16.3535613 48.1994545,16.3559474 48.2011765,16.3567056 48.2025723,16.3571261 48.2038308,16.3578393 48.205176)");
        Geometry point = wkt.read("POINT(16.348243 48.199678)");
        layer.add(shortLineString);
        layer.add(longLineString);

        // First calculate the distances explicitly
        Geometry closestGeom = null;
        double closestDistance = Double.MAX_VALUE;
        System.out.println("Calculating explicit distance to the point " + point + ":");
        for (Geometry geom : new Geometry[] { shortLineString, longLineString }) {
            double distance = point.distance(geom);
            System.out.println("\tDistance " + distance + " to " + geom);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestGeom = geom;
            }
        }
        System.out.println("Found closest: " + closestGeom);
        System.out.println();

        // Now use the SearchClosest class to perform the search for the closest
        System.out.println("Searching for geometries close to " + point);

        GeoPipeline pipeline = GeoPipeline.startNearestNeighborSearch(layer, point.getCoordinate(), 100)
                .sort("Distance")
                .getMin("Distance");
        for (SpatialRecord result : pipeline) {
            System.out.println("\tGot search result: " + result);
            assertEquals("Did not find the closest", closestGeom.toString(), result.getGeometry().toString());
        }

        // Repeat with an envelope
        Envelope env = new Envelope(point.getCoordinate().x, point.getCoordinate().x, point.getCoordinate().y, point.getCoordinate().y);
        env.expandToInclude(shortLineString.getEnvelopeInternal());
        env.expandToInclude(longLineString.getEnvelopeInternal());
        pipeline = GeoPipeline.startNearestNeighborSearch(layer, point.getCoordinate(), env)
                .sort("Distance")
                .getMin("Distance");
        System.out.println("Searching for geometries close to " + point + " within " + env);
        for (SpatialRecord result : pipeline) {
            System.out.println("\tGot search result: " + result);
            assertEquals("Did not find the closest", closestGeom.toString(), result.getGeometry().toString());
        }

        // Repeat with a buffer big enough to work
        double buffer = 0.0001;
        pipeline = GeoPipeline.startNearestNeighborSearch(layer, point.getCoordinate(), buffer)
                .sort("Distance")
                .getMin("Distance");
        System.out.println("Searching for geometries close to " + point + " within buffer " + buffer);
        for (SpatialRecord result : pipeline) {
            System.out.println("\tGot search result: " + result);
            assertEquals("Did not find the closest", closestGeom.toString(), result.getGeometry().toString());
        }

        // Repeat with a buffer too small to work correctly
        //TODO: Since the new Envelope class in graph-collections seems to not have the same bug as the old JTS Envelope, this test case no longer works. We should think of a new test case.
//		buffer = 0.00001;
//		closest = new SearchClosest(point, buffer);
//		System.out.println("Searching for geometries close to " + point + " within buffer " + buffer);
//		layer.getIndex().executeSearch(closest);
//		for (SpatialDatabaseRecord result : closest.getExtendedResults()) {
//			System.out.println("\tGot search result: " + result);
//			// NOTE the test below is negative, because the buffer was badly chosen
//			assertThat("Unexpectedly found the closest", result.getGeometry().toString(), is(not(closestGeom.toString())));
//		}

        // Repeat with the new limit API
        int limit = 10;
        pipeline = GeoPipeline.startNearestNeighborSearch(layer, point.getCoordinate(), limit)
                .sort("Distance")
                .getMin("Distance");
        System.out.println("Searching for geometries close to " + point + " within automatic window designed to get about " + limit + " geometries");
        for (SpatialRecord result : pipeline) {
            System.out.println("\tGot search result: " + result);
            assertThat("Did not find the closest", result.getGeometry().toString(), is(closestGeom.toString()));
        }

    }

}
