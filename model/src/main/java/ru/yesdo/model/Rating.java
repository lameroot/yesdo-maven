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
    private Product product;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
