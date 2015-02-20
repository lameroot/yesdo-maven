package ru.yesdo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.db.repository.MerchantRepository;
import ru.yesdo.db.repository.OfferRepository;
import ru.yesdo.db.repository.ProductRepository;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.graph.repository.OfferGraphRepository;
import ru.yesdo.graph.repository.ProductGraphRepository;
import ru.yesdo.model.*;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.OfferData;

import javax.annotation.Resource;

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

    @Transactional
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
        }

        return offer;
    }
}
