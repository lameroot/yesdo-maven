package ru.yesdo.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.junit.Test;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.Relationship;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.*;
import ru.yesdo.model.data.ContactData;
import ru.yesdo.model.data.OfferData;
import ru.yesdo.model.data.OfferTimeData;

import javax.annotation.Resource;
import java.sql.Time;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by lameroot on 21.03.15.
 */
public class TimeCostServiceTest extends GeneralCommonServiceTest {


    @Test
    @Transactional
    @Rollback(false)
    public void testTimeCost() {
        createWeekDays();

        Activity activity = new Activity();
        activity.setName("activity_one");
        activityGraphRepository.save(activity);
        assertNotNull(activity.getGraphId());

        Merchant merchant = new Merchant();
        merchant.setName("merchant_one");
        merchant.setTitle("merchant_one");
        merchant.addActivity(activity);
        merchantGraphRepository.save(merchant);
        assertNotNull(merchant.getGraphId());

        Product product = new Product();
        product.setCode(UUID.randomUUID().toString());
        product.setTitle("product_one");
        product.setMerchant(merchant);
        productGraphRepository.save(product);
        assertNotNull(product.getGraphId());

        OfferData offerData =
                new OfferData().setAmount(10L).setPublicity(Publicity.PUBLIC).setProductType(ProductType.SERVICE).setContactData(
                        new ContactData().setLocation(10.0, 20.0));
        offerData.addOfferTimes(WeekDay.Days.FRIDAY, new OfferTimeData().start(1000).finish(2000), new OfferTimeData().interval(1200, 1400))
                .addOfferTimes(WeekDay.Days.MONDAY, new OfferTimeData().interval(1200, 1400));


        Offer offer = offerData.toOffer();
        offer.setMerchant(merchant);
        offer.setProduct(product);

        offerGraphRepository.save(offer);
        if ( null != offerData.getOfferTimes() && !offerData.getOfferTimes().isEmpty() ) {
            offer.setOfferWorkTime(JsonUtil.toSafeJson(offerData.getOfferTimes()));
            for (Map.Entry<WeekDay.Days, Set<OfferTimeData>> entry : offerData.getOfferTimes().entrySet()) {
                WeekDay.Days day = entry.getKey();
                Set<OfferTimeData> offerTimeDatas = entry.getValue();
                WeekDay weekDay = weekDayGraphRepository.findBySchemaPropertyValue("day", day);
                for (OfferTimeData offerTimeData : offerTimeDatas) {
                    OfferTime oft = weekDayGraphRepository.createDuplicateRelationshipBetween(weekDay, offer, OfferTime.class, "OFFER_TIME");
                    oft.setStartTime(offerTimeData.getStartTime());
                    oft.setFinishTime(offerTimeData.getFinishTime());
                    oft = neo4jTemplate.save(oft);
                    offer.addOfferTime(oft);
                }
            }
        }
        assertNotNull(offer.getGraphId());

        Result<Contact> contacts = contactGraphRepository.findWithinShape(Contact.LOCATION_INDEX_NAME, new Polygon(new Point(10.0, 20.0), new Point(20.0, 30), new Point(30.0, 40.0)));
        assertNotNull(contacts);

        TimeCost timeCost = TimeCost.duringOneDay(Calendar.getInstance(), TimeCost.createTime(10,0),TimeCost.createTime(22,0),300L);
        Relationship relationship = timeCostService.addTimeCost(offer, timeCost);
        assertNotNull(relationship);

        TimeCost timeCost2 = TimeCost.duringOneDay(Calendar.getInstance(), TimeCost.createTime(12,0),null,350L);
        Relationship relationship2 = timeCostService.addTimeCost(offer, timeCost2);
        assertNotNull(relationship2);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH,2);
        TimeCost timeCost3 = TimeCost.duringSeveralDays(Calendar.getInstance(),tomorrow,TimeCost.createTime(10,0),TimeCost.createTime(22,0),300L);
        Relationship relationship3 = timeCostService.addTimeCost(offer, timeCost3);
        assertNotNull(relationship3);

        TimeCost timeCost4 = TimeCost.duringOneSpecialDay(TimeCost.SpecialDay.MONDAY,TimeCost.createTime(10,0),TimeCost.createTime(22,0),400L);
        Relationship relationship4 = timeCostService.addTimeCost(offer,timeCost4);
        assertNotNull(relationship4);



//        EditableLayer layer = spatialDatabaseService.getOrCreateEditableLayer("timecost");
//        Envelope searchWindow = new Envelope(45.0,49.0,49.0,59.0);
//        GeoPipeline pipeline = GeoPipeline.startNearestNeighborLatLonSearch(layer, new Coordinate(130.0, 140.0, 150.0), searchWindow);
//        for (SpatialRecord spatialRecord : pipeline) {
//            System.out.println(spatialRecord.getGeometry().toString());
//        }

    }
}
