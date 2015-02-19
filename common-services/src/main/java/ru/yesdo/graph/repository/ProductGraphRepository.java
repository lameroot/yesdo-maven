package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.Product;

import java.util.List;

/**
 * Created by lameroot on 22.01.15.
 */
@Repository
public interface ProductGraphRepository extends GraphRepository<Product>, CypherDslRepository<Product> {

    @Query("match (p:Product)<-[r:PRODUCT]-(m)<-[:MERCHANT]-()<-[:ACTIVITY*0..3]-(a:Activity {title: {0}}) where r.amount >= {1} return distinct p")
    public List<Product> findByAmount(String activityTitle, Integer amount);

    public Product findByCode(String code);
}
