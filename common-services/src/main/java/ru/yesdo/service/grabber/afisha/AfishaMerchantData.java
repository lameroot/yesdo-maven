package ru.yesdo.service.grabber.afisha;

import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.OfferData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lameroot on 08.03.15.
 */
class AfishaMerchantData {

    MerchantData merchantData;
    Map<Movie, Set<OfferData>> movieSetMap = new HashMap<>();
}
