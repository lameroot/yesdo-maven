package ru.yesdo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lameroot on 13.12.14.
 * Класс мерчанта, компаниие. Это один из основных классов, который будет использоваться.
 * Должна быть отдельная вкладка для управления мерчантами. Должно быть разграничение по пермиссиям.
 * По сути управление мерчантами должно сводится к созданию и редактированию. Примерно тоже самое что в нашей админке.
 */

@Entity
@Table(name = "merchant")
@NodeEntity
public class Merchant {

    public final static String INDEX_FOR_NAME = "merchant_name";
    public final static String INDEX_FOR_TITLE = "merchant_title";

	@Id
	@SequenceGenerator(name = "merchant_id_gen", sequenceName = "merchant_seq")
	@GeneratedValue(generator = "merchant_id_gen", strategy = GenerationType.SEQUENCE)
    @GraphId
	private Long id;
    @GraphProperty
    @Indexed(indexName = INDEX_FOR_NAME, indexType = IndexType.SIMPLE, unique = true)
    @Column(name = "name", unique = true)
	private String name;
    @GraphProperty
    @Indexed(indexName = INDEX_FOR_TITLE, indexType = IndexType.FULLTEXT)
    private String title;

    @RelatedTo(type = "USER", direction = Direction.OUTGOING)
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<User> users;

    @RelatedTo(type = "PRODUCT", direction = Direction.OUTGOING)
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "merchant", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    @RelatedTo(type = "MERCHANT", direction = Direction.INCOMING)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "activity_merchant", joinColumns = {@JoinColumn(name = "fk_merchant_id",nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "fk_activity_id", nullable = false, updatable = false)})
    private Set<Activity> activities = new HashSet<>();//список активити в которые может вступать мерчант. кол-во активити должно ограничиваться пермиссией

    @RelatedTo(type = "OFFER", direction = Direction.OUTGOING, elementClass = Offer.class)
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "merchant")
    private Set<Offer> offers = new HashSet<>();

    public Offer concludeOffer(Product product, Offer offerData) {
        Offer offer = new Offer();
        offer.setMerchant(this);
        offer.setProduct(product);
        offer.setEnabled(offerData.isEnabled());
        offer.setAmount(offerData.getAmount());
        offer.setExpirationAt(offerData.getExpirationAt());
        offer.setProductType(offerData.getProductType());
        offer.setPublicity(offerData.getPublicity());
        offer.setTimeProduct(offerData.getTimeProduct());
        offer.setContact(offerData.getContact());

        return offer;
    }

    public Merchant() {}
    public Merchant(String name) {
        this.name = name;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @JsonIgnore
    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @JsonIgnore
    public Set<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    public Merchant addActivity(Activity activity) {
        if ( null == activities ) this.activities = new HashSet<>();
        this.activities.add(activity);
        return this;
    }

    //    private Contact contact;//контактная информация для мерчанта
//    private Set<Tag> tags;//список тэгов, по которым может осуществляться поиск н-р, должно ограничиваться пермиссией кол-во
//    private Set<Media> medias;//список меди-ресурсов для данного мерчанта, это могут быть загружаемые видео или картинки
//    private Blog description;//описание компании
//    private Set<Blog> blogs;//список блогов, которые есть у мерчанта
//    private Set<Option> options;//список пермиссий, которыми он обладает
//    private boolean enabled;//доступен или нет
}
