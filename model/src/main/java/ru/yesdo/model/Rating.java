package ru.yesdo.model;

import org.springframework.data.neo4j.annotation.*;

/**
 * User: Krainov
 * Date: 12.02.2015
 * Time: 18:04
 */
@RelationshipEntity
public class Rating {

    @GraphId
    private Long id;
    @StartNode @Fetch
    private User user;
    @EndNode @Fetch
    private Offer offer;
    @GraphProperty
    private int stars;
    @GraphProperty
    private String comment;

    public Rating rate(int stars, String comment) {
        this.stars = stars;
        this.comment = comment;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
