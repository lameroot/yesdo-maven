package ru.yesdo.service;

import org.junit.Test;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.*;
import ru.yesdo.model.data.ContactData;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.OfferData;
import ru.yesdo.model.data.OfferTimeData;

import java.util.Map;
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

        assertEquals(merchantDatas.size(),merchantRepository.count());
        assertEquals(merchantDatas.size(), merchantGraphRepository.count());

        Merchant m21Found = merchantGraphRepository.findByName("m21");
        assertNotNull(m21Found);
        Set<Activity> activities = m21Found.getActivities();
        assertNotNull(activities);
        assertEquals(2,activities.size());
        neo4jTemplate.fetch(activities);
        assertEquals(1,activities.stream().filter(f -> f.getName().equals("a21")).count());
        assertEquals(1,activities.stream().filter(f -> f.getName().equals("a22")).count());

        Merchant m21DbFound = merchantRepository.findByName("m21");
        assertNotNull(m21DbFound);
        assertNotNull(m21DbFound.getActivities());
        assertEquals(2, m21DbFound.getActivities().size());
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testConcludeOffer() throws Exception {
        createWeekDays();
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
    public void testCreateOneOffer() {
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

    }
}
