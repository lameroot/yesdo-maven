package ru.yesdo.service;

import org.geotools.filter.text.cql2.CQLException;
import org.junit.Test;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.graphdb.Relationship;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.*;
import ru.yesdo.model.data.*;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by lameroot on 19.02.15.
 */
public class MerchantServiceTest extends GeneralCommonServiceTest {


    @Test
    @Transactional
    public void testCreateMerchant() {
        createActivities();
        for (MerchantData merchantData : merchantDatas) {
            Merchant merchant = createMerchant(merchantData);
            assertNotNull(merchant);
        }

        assertEquals(merchantDatas.size(), merchantRepository.count());
        assertEquals(merchantDatas.size(), merchantGraphRepository.count());

        Merchant m21Found = merchantGraphRepository.findByName("m21");
        assertNotNull(m21Found);
        Set<Activity> activities = m21Found.getActivities();
        assertNotNull(activities);
        assertEquals(2,activities.size());
        neo4jTemplate.fetch(activities);
        assertEquals(1,activities.stream().filter(f -> f.getName().equals("a21")).count());
        assertEquals(1, activities.stream().filter(f -> f.getName().equals("a22")).count());

        Merchant m21DbFound = merchantRepository.findByName("m21");
        assertNotNull(m21DbFound);
        assertNotNull(m21DbFound.getActivities());
        assertEquals(2, m21DbFound.getActivities().size());
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testConcludeOffer() throws Exception {
        createActivities();
        createMerchants();
        createProducts();

        createOffers();

        assertEquals(offerDatas.size(),offerRepository.count());
        assertEquals(offerDatas.size(),offerGraphRepository.count());

        for (Offer offer : offerRepository.findAll()) {
            System.out.println(offer.getId() + ":" + offer.getMerchant() + ":" + offer.getProduct());
        }

        Merchant m21Found = merchantGraphRepository.findByName("m21");
        assertNotNull(m21Found);
        Set<Offer> offers = m21Found.getOffers();
        assertTrue(offers.size() == 2);
        neo4jTemplate.fetch(offers);
        assertEquals(1, offers.stream().filter(f -> f.getProduct().getTitle().equals("p21")).count());
        assertEquals(1, offers.stream().filter(f -> f.getProduct().getTitle().equals("p22")).count());
        Contact contact = m21Found.getContact();
        assertNotNull(contact);
        neo4jTemplate.fetch(contact);
        assertTrue(contact.getContactParam("test").getValue().equals("this is test"));
    }

    //http://www.lyonwj.com/mapping-the-worlds-airports-with-neo4j-spatial-and-openflights-part-1/
    @Test
    @Transactional
    @Rollback(false)
    public void testCreateRealExample() {

        Activity activity = new Activity();
        activity.setName("activity_one");
        activityGraphRepository.save(activity);
        assertNotNull(activity.getGraphId());

        Merchant merchant1 = new Merchant();
        merchant1.setName("Cinema 1");
        merchant1.setTitle("My cinema 1");
        merchant1.addActivity(activity);
        merchantGraphRepository.save(merchant1);
        assertNotNull(merchant1.getGraphId());

        Product productm1p1 = new Product();
        productm1p1.setCode(UUID.randomUUID().toString());
        productm1p1.setTitle("Film 1");
        productm1p1.setMerchant(merchant1);
        productGraphRepository.save(productm1p1);
        assertNotNull(productm1p1.getGraphId());

        OfferData offerDatam1p1od1 = new OfferData();
        Calendar start1 = Calendar.getInstance();
        Calendar finish1 = Calendar.getInstance();
        finish1.add(Calendar.DAY_OF_MONTH,15);
        TimeCost timeCost1 = TimeCost.duringSeveralDays(start1, finish1, TimeCost.createTime(10, 0), TimeCost.createTime(12, 0), 100L);
        TimeCost timeCost2 = TimeCost.duringSeveralDays(start1, finish1, TimeCost.createTime(12, 0), TimeCost.createTime(14, 0), 200L);
        TimeCost timeCost3 = TimeCost.duringSeveralDays(start1, finish1, TimeCost.createTime(15, 0), TimeCost.createTime(17, 0), 200L);
        TimeCost timeCost4 = TimeCost.duringSeveralDays(start1, finish1, TimeCost.createTime(18, 0), TimeCost.createTime(20, 0), 300L);
        TimeCost timeCost5 = TimeCost.duringSeveralDays(start1, finish1, TimeCost.createTime(21, 0), TimeCost.createTime(23, 0), 300L);

        offerDatam1p1od1.addTimeCost(timeCost1).addTimeCost(timeCost2).addTimeCost(timeCost3).addTimeCost(timeCost4).addTimeCost(timeCost5);

        Offer offer1 = offerDatam1p1od1.toOffer();
        offer1.setMerchant(merchant1);
        offer1.setProduct(productm1p1);
        offerGraphRepository.save(offer1);

        for (TimeCost timeCost : offerDatam1p1od1.getTimeCosts()) {
            Relationship r = timeCostService.addTimeCost(offer1, timeCost);
            System.out.println("r id = " + r.getId());
        }

        //
        Product productm1p2 = new Product();
        productm1p2.setCode(UUID.randomUUID().toString());
        productm1p2.setTitle("Film 2");
        productm1p2.setMerchant(merchant1);
        productGraphRepository.save(productm1p2);
        assertNotNull(productm1p2.getGraphId());

        OfferData offerDatam1p2od2 = new OfferData();
        Calendar start11 = Calendar.getInstance();
        Calendar finish11 = Calendar.getInstance();
        finish11.add(Calendar.DAY_OF_MONTH,10);
        TimeCost timeCost11 = TimeCost.duringSeveralDays(start11, finish11, TimeCost.createTime(10, 0), TimeCost.createTime(12, 0), 200L);
        TimeCost timeCost21 = TimeCost.duringSeveralDays(start11, finish11, TimeCost.createTime(12, 0), TimeCost.createTime(14, 0), 200L);
        TimeCost timeCost31 = TimeCost.duringSeveralDays(start11, finish11, TimeCost.createTime(15, 0), TimeCost.createTime(17, 0), 300L);
        TimeCost timeCost41 = TimeCost.duringSeveralDays(start11, finish11, TimeCost.createTime(18, 0), TimeCost.createTime(20, 0), 400L);
        TimeCost timeCost51 = TimeCost.duringSeveralDays(start11, finish11, TimeCost.createTime(21, 0), TimeCost.createTime(23, 0), 400L);
        TimeCost timeCost61 = TimeCost.duringSeveralDays(start11, finish11, TimeCost.createTime(23, 0), TimeCost.createTime(2, 0), 400L);

        offerDatam1p2od2.addTimeCost(timeCost11).addTimeCost(timeCost21).addTimeCost(timeCost31).addTimeCost(timeCost41).addTimeCost(timeCost51).addTimeCost(timeCost61);

        Offer offer2 = offerDatam1p1od1.toOffer();
        offer2.setMerchant(merchant1);
        offer2.setProduct(productm1p2);
        //offerGraphRepository.save(offer2);

        for (TimeCost timeCost : offerDatam1p2od2.getTimeCosts()) {
            //Relationship r = timeCostService.addTimeCost(offer2, timeCost);
            //System.out.println("r id = " + r.getId());
        }

        Calendar searchStart = Calendar.getInstance();
        searchStart.add(Calendar.DAY_OF_MONTH,2);
        Calendar searchEnd = Calendar.getInstance();
        searchEnd.add(Calendar.DAY_OF_MONTH, 6);

        StopWatch watch = new StopWatch("test");
        watch.start();
        try {
            //List<SpatialDatabaseRecord> records = timeCostService.findBy(searchStart, searchEnd, 12.15, 17.59, 100L, 200L);
            //Set<Offer> offers = timeCostService.toOffers(records);
//            Set<Offer> offers = timeCostService.findOffersBy(searchStart, searchEnd, 12.15, 17.59, 100L, 200L);
//            for (Offer offer : offers) {
//                System.out.println("off = " + offer);
//
//            }
            Set<OfferIdentificator> ids = timeCostService.findOfferIdsBy(searchStart, searchEnd, 12.15, 17.59, 100L, 200L);
            for (OfferIdentificator id : ids) {
                System.out.println(id);
            }
        } catch (CQLException e) {
            e.printStackTrace();
        }
        watch.stop();
        System.out.println(watch.toString());
    }
}
