package ru.yesdo.service.grabber;

import org.springframework.data.neo4j.support.Neo4jTemplate;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.service.ActivityService;
import ru.yesdo.service.MerchantService;

import javax.annotation.Resource;

/**
 * Created by lameroot on 08.03.15.
 */
public abstract class AbstractGrabber implements Grabber {

    @Resource
    protected ActivityService activityService;
    @Resource
    protected Neo4jTemplate neo4jTemplate;
    @Resource
    protected ActivityGraphRepository activityGraphRepository;
    @Resource
    protected MerchantService merchantService;
    @Resource
    protected MerchantGraphRepository merchantGraphRepository;
}
