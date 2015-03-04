package ru.yesdo.service.grabber;

import org.junit.Test;
import ru.yesdo.AbstractCommonServiceTest;
import ru.yesdo.GeneralCommonServiceTest;


import javax.annotation.Resource;

/**
 * Created by lameroot on 04.03.15.
 */
public class AfishaGrabberTest extends AbstractCommonServiceTest {

    @Resource
    private AfishaGrabber afishaGrabber;

    @Test
    public void testGetUnde() throws Exception {
        assertNotNull(afishaGrabber);
    }
}
