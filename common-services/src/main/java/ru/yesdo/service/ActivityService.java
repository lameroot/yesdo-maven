package ru.yesdo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import javax.annotation.Resource;

/**
 * Created by lameroot on 18.02.15.
 */
@Service
public class ActivityService {

    @Resource
    private ActivityRepository activityRepository;
    @Resource
    private ActivityGraphRepository activityGraphRepository;

    @Transactional
    public Activity create(ActivityData activityData) {
        if ( null != activityRepository.findByName(activityData.getName()) ) throw new AlreadyExistException(activityData.getName());
        Activity activity = new Activity();
        activity.setTitle(activityData.getTitle());
        activity.setName(activityData.getName());
        if ( null != activityData.getParents() ) {
            for (Activity parent : activityData.getParents()) {
                if ( null == (parent = activityRepository.findByName(parent.getName())) )  throw new IllegalArgumentException("Activity with name: " + parent.getName() + " not found in db");
                activity.addParent(parent);
            }
        }

        activityRepository.save(activity);
        if ( activityData.isPartial() )  {
            activityGraphRepository.save(activity);
        }
        return activity;
    }

}
