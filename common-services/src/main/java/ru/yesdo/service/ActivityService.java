package ru.yesdo.service;

import org.neo4j.cypherdsl.expression.NumericExpression;
import org.neo4j.cypherdsl.expression.StartExpression;
import org.neo4j.cypherdsl.grammar.Execute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.typerepresentation.IndexBasedNodeTypeRepresentationStrategy;
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
import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * Created by lameroot on 18.02.15.
 */
@Service
public class ActivityService {

    @Resource
    private ActivityRepository activityRepository;
    @Resource
    private ActivityGraphRepository activityGraphRepository;

    private static final String queryFindChildByDeep = "START activity=node:activity_name(name={activityRootName}) MATCH p=(activity)-[:ACTIVITY*0..%s]->(ch) RETURN ch";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Activity create(ActivityData activityData) {
        if ( null != activityRepository.findByName(activityData.getName()) ) throw new AlreadyExistException(activityData.getName());
        Activity activity = new Activity();
        activity.setTitle(activityData.getTitle());
        activity.setName(activityData.getName());
        for (Map.Entry<String, Object> entry : activityData.getParams().entrySet()) {
            activity.addParam(entry.getKey(),entry.getValue());
        }
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
        String query = String.format(queryFindChildByDeep, null != deep ? deep : "");
        return activityGraphRepository.query(query, map("activityRootName", activityRootName));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Activity update(Activity activity) {
        if ( null == activity || null == activity.getId() || null == activity.getGraphId() )
            throw new IllegalArgumentException("Activity: " + activity + " has not correct data");

        Activity dbActivity = activityRepository.findOne(activity.getId());
        if ( null == dbActivity )
            throw new IllegalArgumentException("Unable to find activity by id: " + activity.getId() + " in database");
        Activity graphActivity = activityGraphRepository.findOne(activity.getGraphId());
        if ( null == graphActivity )
            throw new IllegalArgumentException("Unable to find activity by graphId: " + activity.getGraphId() + " in graph database");

        dbActivity.setTitle(activity.getTitle());


        return null;
    }

    public String searchFreeName(String name) {
        Activity activity = null;
        do {
            activity = activityRepository.findByName(name);
        } while (activity == null);
        return activity.getName();
    }

    /*
    //https://github.com/cpoepke/demos/blob/master/neo4j-spring-data-querydsl-example/src/test/java/de/cpoepke/demos/neo4j/querydsl/UserRepositoryTest.java
    private StartExpression domainStartNodes(String name, Class clazz) {

        Execute query =
                start(lookup(identifier("activity"),identifier(Activity.INDEX_FOR_NAME),identifier("name"),param("activityRootName") )).
                match(path("p", node("activity")
                        .out("ACTIVITY").hops(0, 1).node("ch"))).returns(identifier("ch"))
                ;

        return lookup(name, IndexBasedNodeTypeRepresentationStrategy.INDEX_NAME, IndexBasedNodeTypeRepresentationStrategy.INDEX_KEY, clazz.getCanonicalName() );
    }
    */

}
