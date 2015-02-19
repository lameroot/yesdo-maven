package ru.yesdo.model;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import javax.persistence.*;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 14:57
 */
@Entity
@Table(name = "partial")
@NodeEntity(partial = true)
public class PartialModel {

    @GraphId
    @Id
    @SequenceGenerator(name = "partial_id_gen", sequenceName = "partial_seq")
    @GeneratedValue(generator = "partial_id_gen", strategy = GenerationType.SEQUENCE)
    private Long id;

    @GraphProperty
    @Column(name = "title")
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartialModel{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
