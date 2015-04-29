package ru.yesdo.service.grabber;

import org.geotools.filter.text.cql2.CQLException;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import ru.yesdo.AbstractCommonServiceTest;
import ru.yesdo.model.Offer;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by lameroot on 26.04.15.
 */
public class GrabberSearcherTest extends AbstractCommonServiceTest {

    @Test
    @Transactional
    public void findProducts() {
        Calendar searchStart = Calendar.getInstance();
        searchStart.add(Calendar.DAY_OF_MONTH,1);
        Calendar searchEnd = Calendar.getInstance();
        searchEnd.add(Calendar.DAY_OF_MONTH, 1);

        StopWatch watch = new StopWatch("test");
        watch.start();

        try {
            Set<Offer> offers = timeCostService.findOffersBy(searchStart, searchEnd, 12.00, 16.30, 20000L, 50000L);
            for (Offer offer : offers) {
                System.out.println(offer + ":" + offer.getProduct() + ":" + offer.getMerchant() + ":" + offer.getTimeCosts());
            }
        } catch (CQLException e) {
            e.printStackTrace();
        }
        watch.stop();
        System.out.println(watch.toString());
    }

}
