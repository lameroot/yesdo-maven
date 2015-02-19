package ru.yesdo.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.Product;

/**
 * User: Krainov
 * Date: 05.02.2015
 * Time: 19:02
 */
@Repository
public interface ProductRepository extends CrudRepository<Product,Long> {
}
