package ru.yesdo.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * User: Krainov
 * Date: 13.02.2015
 * Time: 17:54
 */
@NodeEntity
@TypeAlias("OFFER_CONTACT")
public class OfferContact extends Contact {


    @Fetch
    @RelatedTo(direction = Direction.INCOMING, type = "CONTACT")
    private Offer offer;

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

}
