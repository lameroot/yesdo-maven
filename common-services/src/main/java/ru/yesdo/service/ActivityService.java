package ru.yesdo.service;

import org.neo4j.cypherdsl.grammar.Execute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.cypherdsl.CypherQuery.*;
import static org.neo4j.cypherdsl.querydsl.CypherQueryDSL.*;

/**
 * Created by lameroot on 18.02.15.
 */
@Service
public class ActivityService {

    @Resource
    private ActivityRepository activityRepository;
    @Resource
    private ActivityGraphRepository activityGraphRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    public Page<Activity> findAll(Pageable pageableRequest) {
        return activityRepository.findAll(pageableRequest);
    }

    public Result<Activity> findChildByDeep(String activityRootName, Integer deep) {
        Execute query =
                start(lookup("activity",Activity.INDEX_FOR_NAME,"name",activityRootName )).
                match(path("p",node("activity")
                .out("ACTIVITY").hops(0,deep).node("ch"))).returns(identifier("ch"))
                ;
        System.out.println(query.toString());
        Map<String,Object> map = new HashMap<>();
        map.put("activityRootName",activityRootName);
        Result<Activity> activities = activityGraphRepository.query(query,  map);
        return activities;
    }

}
