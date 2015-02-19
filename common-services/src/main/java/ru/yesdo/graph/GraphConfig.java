package ru.yesdo.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 16:36
 */
@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "ru.yesdo.graph.repository")
@ComponentScan("ru.yesdo.graph.service")
public class GraphConfig extends org.springframework.data.neo4j.config.Neo4jConfiguration {

    private static final String DB_PATH = "data/graph.db";
    public GraphConfig() {
        setBasePackage("ru.yesdo.model");
    }

    @Bean
    public GraphDatabaseService graphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    }

}
