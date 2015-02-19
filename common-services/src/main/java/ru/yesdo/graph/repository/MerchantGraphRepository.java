package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.Product;

import java.util.Set;

/**
 * Created by lameroot on 22.01.15.
 */
@Repository
public interface MerchantGraphRepository extends GraphRepository<Merchant>, RelationshipOperationsRepository<Merchant> {

    public Merchant findByName(String name);

    @Query("start merchant=node({0}) match merchant-[:PRODUCT_IN]->product return product")
    public Set<Product> findProducts(Merchant merchant);
}
