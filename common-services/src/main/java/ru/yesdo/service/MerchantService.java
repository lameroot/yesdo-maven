package ru.yesdo.service;

import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.db.repository.MerchantRepository;
import ru.yesdo.db.repository.OfferRepository;
import ru.yesdo.db.repository.ProductRepository;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.graph.repository.*;
import ru.yesdo.model.*;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.OfferData;
import ru.yesdo.model.data.OfferTimeData;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * Created by lameroot on 18.02.15.
 */
@Service
public class MerchantService {

    @Resource
    private MerchantRepository merchantRepository;
    @Resource
    private MerchantGraphRepository merchantGraphRepository;
    @Resource
    private ActivityRepository activityRepository;
    @Resource
    private ActivityGraphRepository activityGraphRepository;
    @Resource
    private ProductRepository productRepository;
    @Resource
    private ProductGraphRepository productGraphRepository;
    @Resource
    private OfferRepository offerRepository;
    @Resource
    private OfferGraphRepository offerGraphRepository;
    @Resource
    private Neo4jTemplate neo4jTemplate;
    @Resource
    private WeekDayGraphRepository weekDayGraphRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Merchant create(MerchantData merchantData) {
        if ( null != merchantRepository.findByName(merchantData.getName()) ) throw new AlreadyExistException(merchantData.getName());
        Merchant merchant = new Merchant(merchantData.getName());
        merchant.setTitle(merchantData.getTitle());
        for (Activity activity : merchantData.getActivities()) {
            if ( null == (activity = activityRepository.findByName(activity.getName())) )  throw new IllegalArgumentException("Activity with name: " + activity.getName() + " not found in db");
            if ( merchantData.isPartial() && null == activityGraphRepository.findByName(activity.getName()) )
                throw new IllegalArgumentException("Activity with name: " + activity.getName() + " not found in graph database");
            merchant.addActivity(activity);
        }
        merchantRepository.save(merchant);
        if ( merchantData.isPartial() ) {
            merchantGraphRepository.save(merchant);
        }
        return merchant;
    }

    @Transactional
    public Offer concludeOffer(Merchant merchant, Product product, OfferData offerData) {
        if ( null == (merchant = merchantRepository.findOne(merchant.getId())) )
            throw new IllegalArgumentException("Unable to find merchant with id: " + merchant.getName());
        if ( offerData.isPartial() && null == merchantGraphRepository.findOne(merchant.getGraphId()) )
            throw new IllegalArgumentException("Unable to find merchant with id: " + merchant.getName() + " in graph database");

        if ( null == (product = productRepository.findOne(product.getId())) )
            throw new IllegalArgumentException("Unable to find product with id: " + product.getId());
        if ( offerData.isPartial() && null == productGraphRepository.findOne(product.getGraphId()) )
            throw new IllegalArgumentException("Unable to find product with id: " + product.getId() + " in graph database");

        Offer offer = offerData.toOffer();
        offer.setMerchant(merchant);
        offer.setProduct(product);

        offerRepository.save(offer);
        if ( offerData.isPartial() ) {
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
        }


        return offer;
    }
}
