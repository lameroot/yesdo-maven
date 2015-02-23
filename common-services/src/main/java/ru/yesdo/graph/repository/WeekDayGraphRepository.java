package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import ru.yesdo.model.WeekDay;

/**
 * Created by lameroot on 23.02.15.
 */
public interface WeekDayGraphRepository extends GraphRepository<WeekDay>, RelationshipOperationsRepository<WeekDay>{
}
