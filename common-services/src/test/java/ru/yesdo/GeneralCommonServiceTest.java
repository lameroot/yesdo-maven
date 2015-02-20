package ru.yesdo;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import ru.yesdo.db.JpaConfigTest;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.graph.GraphConfigTest;

import javax.annotation.Resource;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 16:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
        JpaConfigTest.class,
        GraphConfigTest.class,
        CommonServicesConfig.class
})
@ActiveProfiles(value = "test")
public class GeneralCommonServiceTest extends TestCase {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
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

    @Test
    public void testExist() {
        assertNotNull(applicationContext);
    }
}
