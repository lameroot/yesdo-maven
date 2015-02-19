package ru.yesdo.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lameroot on 25.01.15.
 */
@NodeEntity
public class TestNode {

    @GraphId
    private Long id;
    @GraphProperty
    private String title;
    @RelatedTo(type = "test", direction = Direction.OUTGOING)
    private Set<TestNode> parents = new HashSet<>();
    @RelatedTo(type = "test", direction = Direction.INCOMING)
    private Set<TestNode> child = new HashSet<>();

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

    public Set<TestNode> getParents() {
        return parents;
    }

    public void setParents(Set<TestNode> parents) {
        this.parents = parents;
    }

    public Set<TestNode> getChild() {
        return child;
    }

    public void setChild(Set<TestNode> child) {
        this.child = child;
    }
}
