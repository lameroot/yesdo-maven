package ru.yesdo.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.Activity;

/**
 * User: Krainov
 * Date: 05.02.2015
 * Time: 19:00
 */
@Repository
public interface ActivityRepository extends CrudRepository<Activity,Long> {
}
