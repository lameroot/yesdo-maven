package ru.yesdo;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import ru.yesdo.db.JpaConfigTest;
import ru.yesdo.db.repository.*;
import ru.yesdo.graph.GraphConfigTest;
import ru.yesdo.graph.repository.*;
import ru.yesdo.service.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by lameroot on 04.03.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
        JpaConfigTest.class,
        GraphConfigTest.class,
        CommonServicesConfig.class
})
@ActiveProfiles(value = "test")
public class AbstractCommonServiceTest extends TestCase {

    @Resource
    protected ApplicationContext applicationContext;
    @Autowired(required = false)
    protected Neo4jTemplate neo4jTemplate;
    @Resource
    protected PlatformTransactionManager transactionManager;
    @Resource
    protected GraphDatabaseService graphDatabaseService;
    @Resource
    protected SpatialDatabaseService spatialDatabaseService;
    @PersistenceContext
    protected EntityManager entityManager;
    @Resource
    protected ActivityRepository activityRepository;
    @Resource
    protected ActivityGraphRepository activityGraphRepository;
    @Resource
    protected ActivityService activityService;
    @Resource
    protected MerchantRepository merchantRepository;
    @Resource
    protected MerchantGraphRepository merchantGraphRepository;
    @Resource
    protected MerchantService merchantService;
    @Resource
    protected ProductService productService;
    @Resource
    protected ProductRepository productRepository;
    @Resource
    protected ProductGraphRepository productGraphRepository;
    @Resource
    protected UserRepository userRepository;
    @Resource
    protected UserGraphRepository userGraphRepository;
    @Resource
    protected UserService userService;
    @Resource
    protected OfferRepository offerRepository;
    @Resource
    protected OfferGraphRepository offerGraphRepository;
    @Resource
    protected GeoDataImporter geoDataImporter;
    @Resource
    protected ContactGraphRepository contactGraphRepository;
    @Resource
    protected TimeCostService timeCostService;

    @Test
    public void testExist() {
        assertNotNull(activityGraphRepository);
    }

}
