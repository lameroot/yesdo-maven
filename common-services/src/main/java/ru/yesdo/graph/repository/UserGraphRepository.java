package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.User;

/**
 * User: Krainov
 * Date: 12.02.2015
 * Time: 18:11
 */
@Repository
public interface UserGraphRepository extends GraphRepository<User>, RelationshipOperationsRepository<User> {
}
