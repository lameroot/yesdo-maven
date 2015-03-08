package ru.yesdo.service.grabber;

import java.io.IOException;

/**
 * Created by lameroot on 08.03.15.
 */
public interface Grabber {

    public void grabActivities();
    public void grabMerchants(Integer countForOneActivity) throws Exception;
    public void grabProductAndOffers(boolean onlyProduct) throws Exception;
//    public void grabProducts();
//    public void grabOffers();
}
