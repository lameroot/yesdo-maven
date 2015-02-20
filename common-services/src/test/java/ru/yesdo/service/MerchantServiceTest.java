package ru.yesdo.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.GeneralCommonServiceDbTest;
import ru.yesdo.model.*;
import ru.yesdo.model.data.ActivityData;
import ru.yesdo.model.data.ContactData;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.OfferData;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Created by lameroot on 19.02.15.
 */
public class MerchantServiceTest extends GeneralCommonServiceDbTest {


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
    public void testConcludeOffer() {
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
    }
}
