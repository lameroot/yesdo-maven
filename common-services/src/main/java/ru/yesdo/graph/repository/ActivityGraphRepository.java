package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.Activity;

/**
 * Created by lameroot on 21.01.15.
 * http://docs.spring.io/spring-data/data-neo4j/docs/3.2.1.RELEASE/reference/html/#reference_cross-store
 */
@Repository
public interface ActivityGraphRepository extends GraphRepository<Activity> {

    public Activity findByName(String name);
    public Activity findByTitle(String title);//possible is not uniqeu
}
