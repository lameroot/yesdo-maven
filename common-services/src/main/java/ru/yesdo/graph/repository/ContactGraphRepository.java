package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.repository.SpatialRepository;
import org.springframework.data.repository.CrudRepository;
import ru.yesdo.model.Contact;

/**
 * User: Krainov
 * Date: 13.02.2015
 * Time: 18:21
 */
public interface ContactGraphRepository extends CrudRepository<Contact,Long>, SpatialRepository<Contact> {
}
