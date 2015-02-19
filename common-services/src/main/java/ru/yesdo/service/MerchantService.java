package ru.yesdo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.db.repository.MerchantRepository;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.model.Activity;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.data.MerchantData;

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
}
