package ru.yesdo.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lameroot on 13.12.14.
 * Активити - род занятий, так как например спорт, парашютный спорт, танцы, боевые искусства итд.
 * Активити могут в себя вклчать любое кол-во дочерних элементов, и так же могут иметь любое кол-во родителей
 * Так например: если мы берём родителя: спорт, то его дочерними элементами являются н-р танцы : [брэкданс, танго: [спортивное танго, латинское танго]],
 * боевые исскуства : [карате: [карате-до, кькушенкай], бокс, самбо] итд. То есть в данном примере активити спорт имеет
 * два дочерних элемента, которые имеют под собой своих детей. Но эти дети имеют помимо своих основных родителей ещ> также и спорт,
 * то есть н-р карате-до имеет родителя как карате, так и боевые искусства, так и спорт. Такая связь необходима для поиска.
 * Также построена система и с мерчантами и с продуктами. Каждый мерчант может находится в нескольких активити, это опять таки
 * надо для поиска. С продуктами такая же система.
 * Данная сущность должна хранится в БД, но поиск должен быть построен на основе графов, используем neo4j . то есть помимо данных,
 * которые храним в БД, необходимо также сохранять граф узлов. Используем для этого spring-data-neo4j .
 * !!!На данном этапе делаем только сохранение в БД с помощью хибернайта и spring-data .
 */

@NodeEntity(partial = false)
@Entity
@Table(name = "activity")
public class Activity {

    public final static String ROOT_TITLE = "root_activity";
    public final static String INDEX_FOR_NAME = "activity_name";
    public final static String INDEX_FOR_TITLE = "activity_title";

    @Id
    @SequenceGenerator(name = "activity_id_gen", sequenceName = "activity_seq")
    @GeneratedValue(generator = "activity_id_gen", strategy = GenerationType.SEQUENCE)
    @GraphProperty(propertyName = "db_id")
    private Long id;

    @GraphId
    @Column(name = "graph_id")
    private Long graphId;

    @GraphProperty
    @Indexed(indexName = INDEX_FOR_NAME,indexType = IndexType.SIMPLE, unique = true)
    @Column(unique = true)
    private String name;

    @GraphProperty
    @Indexed(indexName = INDEX_FOR_TITLE, indexType = IndexType.FULLTEXT)
    @Column
    private String title;

    @RelatedTo(direction = Direction.OUTGOING, type = "ACTIVITY")
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "activity_family",joinColumns = {@JoinColumn(name = "fk_child_id",updatable = false)},
    inverseJoinColumns = {@JoinColumn(name = "fk_parent_id",nullable = false,updatable = false)})
    private Set<Activity> parents;//список родителей

    @RelatedTo(direction = Direction.INCOMING, type = "ACTIVITY")
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "parents", cascade = CascadeType.ALL)
    private Set<Activity> child;//список дочерних

    @RelatedTo(type = "MERCHANT", direction = Direction.OUTGOING)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "activities")
    private Set<Merchant> merchants;//список мерчантов, которые находятся в этой активити


    public Activity() {
    }

    public Activity(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Activity> getParents() {
        return parents;
    }

    public void setParents(Set<Activity> parents) {
        this.parents = parents;
    }

    public Set<Activity> getChild() {
        return child;
    }

    public void setChild(Set<Activity> child) {
        this.child = child;
    }

    public Set<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(Set<Merchant> merchants) {
        this.merchants = merchants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Activity addParent(Activity activity) {
        if ( null == this.parents ) this.parents = new HashSet<>();
        this.parents.add(activity);
        return this;
    }

    //todo: протестировать
    public Activity addChildren(Activity activity) {
        if ( null == this.child ) this.child = new HashSet<>();
        activity.addParent(this);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Activity{");
        sb.append("id=").append(id);
        sb.append(", graphId=").append(graphId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
