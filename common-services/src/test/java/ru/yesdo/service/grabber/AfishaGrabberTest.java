package ru.yesdo.service.grabber;

import org.junit.Test;
import ru.yesdo.AbstractCommonServiceTest;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.service.grabber.afisha.AfishaGrabber;


import javax.annotation.Resource;

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
}
