package ru.yesdo.service;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.transaction.Neo4jTransactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        Activity activity = new Activity();
        activity.setTitle(activityData.getTitle());
        activity.setName(activityData.getName());
        activity.setParents(activityData.getParents());

        activityRepository.save(activity);
        if ( activityData.isPartial() )  {
            activityGraphRepository.save(activity);
        }
        return activity;
    }

}
