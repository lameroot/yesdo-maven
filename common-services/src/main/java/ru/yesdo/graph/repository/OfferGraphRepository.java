package ru.yesdo.graph.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import ru.yesdo.model.Offer;

/**
 * Created by lameroot on 12.02.15.
 */
public interface OfferGraphRepository extends GraphRepository<Offer> {
}
