package ru.yesdo.graph;

import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.JtaTransactionManagerFactoryBean;
import org.springframework.data.neo4j.cross_store.config.CrossStoreNeo4jConfiguration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 17:38
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableNeo4jRepositories(basePackages = "ru.yesdo.graph.repository")
@Profile("test")
public class GraphConfigTest
        extends org.springframework.data.neo4j.config.Neo4jConfiguration
    //extends CrossStoreNeo4jConfiguration
{

    @Resource
    private EntityManagerFactory entityManagerFactory;

    private static String neo4jHome = "/usr/local/Cellar/neo4j/2.1.6/libexec/";
    //private static String neo4jHome = "D:\\Users\\krainov\\Documents\\opt\\neo4j-community-2.1.6\\";

    //private static final String DB_PATH = "data/graph.db";
    private static final String DB_PATH = neo4jHome + "data/graph.db";

    //private static final String DB_PATH = "D:/Users/krainov/Documents/opt/neo4j-community-2.1.6/data/graph.db";

    public GraphConfigTest() {
        setBasePackage("ru.yesdo.model");
    }

    @Bean
    public PlatformTransactionManager neo4jTransactionManager() throws Exception {
        JtaTransactionManager jtaTm = new JtaTransactionManagerFactoryBean( getGraphDatabaseService() ).getObject();
        if (null != entityManagerFactory) {
            JpaTransactionManager jpaTm = new JpaTransactionManager(entityManagerFactory);
            return new ChainedTransactionManager(jpaTm, jtaTm);
        }
        else {
            return jtaTm;
        }
    }

    @Bean
    public GraphDatabaseService graphDatabaseService() {
        //return new TestGraphDatabaseFactory().newImpermanentDatabase();
        return new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    }

    @Bean
    public SpatialDatabaseService spatialDatabaseService() {
        SpatialDatabaseService spatialDatabaseService = new SpatialDatabaseService(graphDatabaseService());
        return spatialDatabaseService;
    }


}
