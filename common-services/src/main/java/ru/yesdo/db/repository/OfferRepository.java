package ru.yesdo.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yesdo.model.Offer;

/**
 * Created by lameroot on 08.02.15.
 */
@Repository
public interface OfferRepository extends CrudRepository<Offer, Long> {
}
