package ru.yesdo.service.grabber;

import org.geotools.filter.text.cql2.CQLException;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import ru.yesdo.AbstractCommonServiceTest;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.Activity;
import ru.yesdo.model.Product;
import ru.yesdo.service.grabber.afisha.AfishaGrabber;


import javax.annotation.Resource;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by lameroot on 04.03.15.
 */
public class AfishaGrabberTest extends GeneralCommonServiceTest {

    @Resource
    private Grabber afishaGrabber;

    @Test
    public void testGrabActivities() {
        afishaGrabber.grabActivities();

        long countInDb = activityRepository.count();
        long countInGraph = activityGraphRepository.count();
        System.out.println("count in db: " + countInDb);
        System.out.println("count in graph: " + countInGraph);

        activityGraphRepository.findByName("cinema");
    }

    @Test
    public void testGrabMerchants() throws Exception {
        afishaGrabber.grabActivities();
        afishaGrabber.grabMerchants(10);

        long countInDb = merchantRepository.count();
        long countInGraph = merchantGraphRepository.count();
        System.out.println("count in db: " + countInDb);
        System.out.println("count in graph: " + countInGraph);
    }

    @Test
    public void testGrabProductsAndOffers() throws Exception {
        afishaGrabber.grabActivities();
        afishaGrabber.grabMerchants(1);

        long countInDb = merchantRepository.count();
        long countInGraph = merchantGraphRepository.count();
        System.out.println("count in db: " + countInDb);
        System.out.println("count in graph: " + countInGraph);

        afishaGrabber.grabProductAndOffers(false, 1);
    }


}
