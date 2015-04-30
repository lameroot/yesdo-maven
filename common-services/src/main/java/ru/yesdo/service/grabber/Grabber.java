package ru.yesdo.service.grabber;

import ru.yesdo.model.Activity;
import ru.yesdo.model.Merchant;

import java.io.IOException;
import java.util.Set;

/**
 * Created by lameroot on 08.03.15.
 */
public interface Grabber {

    public Set<Activity> grabActivities();
    public Set<Merchant> grabMerchants(Integer countForOneActivity) throws Exception;
    public void grabProductAndOffers(boolean onlyProduct, int countDaysFromCurrent) throws Exception;
//    public void grabProducts();
//    public void grabOffers();
}
