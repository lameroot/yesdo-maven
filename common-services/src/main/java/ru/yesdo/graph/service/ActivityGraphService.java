package ru.yesdo.graph.service;

import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by lameroot on 24.01.15.
 */
@Service
public class ActivityGraphService {

    @Resource
    private ActivityGraphRepository activityGraphRepository;
    @Resource
    private Neo4jTemplate neo4jTemplate;

    public Activity create(ActivityData activityData) {
        try {
            Activity activity = new Activity();
            activity.setTitle(activityData.getTitle());
            activity.setName(activityData.getName());
            //activity.setParents(activityData.getParents());
            activityGraphRepository.save(activity);
            return activity;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    
    public void fillTestData(int countChildInOneParent, int deepChild) {
        Set<Activity> rootLevel = new HashSet<>();
        rootLevel.add(null);
        int i = 0;
        do {
            rootLevel = ff(rootLevel,countChildInOneParent,i);
            i++;
        } while (i < deepChild );

    }

    private Set<Activity> ff(Collection<Activity> parents, int countChildInOneParent, int levelDeep) {
        Set<Activity> newParents = new HashSet<>();
        for (Activity parent : parents) {
            for (int i = 0; i < countChildInOneParent; i++) {
                Activity activity = createSimpleActivity("titleLevel: " + levelDeep);
                //activity.setParent(parent);
                activityGraphRepository.save(activity);
                newParents.add(activity);
            }
        }
        return newParents;


    }

    private Activity createSimpleActivity(String prefixTitle) {
        String uniqueIndex = UUID.randomUUID().toString();
        String titleParent = prefixTitle + ":" + uniqueIndex;
        Activity activityParent = new Activity(titleParent);
        activityParent.setTitle(titleParent);
        activityGraphRepository.save(activityParent);
        return activityParent;
    }
}
