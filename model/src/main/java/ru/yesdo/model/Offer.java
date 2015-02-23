package ru.yesdo.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by lameroot on 08.02.15.
 */
@NodeEntity
@Entity
@Table(name = "offer")
public class Offer {

    @Id
    @SequenceGenerator(name = "offer_id_gen", sequenceName = "offer_seq")
    @GeneratedValue(generator = "offer_id_gen", strategy = GenerationType.SEQUENCE)
    @GraphProperty(propertyName = "db_id")
    private Long id;

    @GraphId
    @Column(name = "graph_id")
    private Long graphId;

    @RelatedTo(direction = Direction.INCOMING,type = "OFFER")
    @Fetch
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_merchant_id", nullable = false)
    private Merchant merchant;

    @RelatedTo(direction = Direction.OUTGOING, type = "OFFER")
    @Fetch
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_product_id", nullable = false)
    private Product product;

    @GraphProperty
    @Column
    private Long amount;

    @GraphProperty
    @Column
    private boolean enabled;//доступен или нет.

    @GraphProperty
    @Enumerated(EnumType.STRING)
    private Publicity publicity;//публичность данного продукта, он может быть скрытый, может быть приватный, публичный, только для избранных

    @GraphProperty
    @Transient
    private TimeProduct timeProduct;//время в которое можно воспользоваться услугой

    @Transient
    //@RelatedTo(direction = Direction.OUTGOING, type = "OFFER_DAY")
    private List<OfferDay> offerDays = new ArrayList<>();

    @Transient
    @RelatedToVia(direction = Direction.INCOMING, elementClass = OfferTime.class)
    private Set<OfferTime> offerTimes = new HashSet<>();

    @GraphProperty
    @Enumerated
    private ProductType productType;//тип продукта

    @GraphProperty
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_at")
    private Date expirationAt;//дата истечения возможности использования продуктом

    @RelatedTo(type = "CONTACT", direction = Direction.OUTGOING)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contact_id", nullable = true)
    private Contact contact;

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

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Publicity getPublicity() {
        return publicity;
    }

    public void setPublicity(Publicity publicity) {
        this.publicity = publicity;
    }

    public TimeProduct getTimeProduct() {
        return timeProduct;
    }

    public void setTimeProduct(TimeProduct timeProduct) {
        this.timeProduct = timeProduct;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Date getExpirationAt() {
        return expirationAt;
    }

    public void setExpirationAt(Date expirationAt) {
        this.expirationAt = expirationAt;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<OfferDay> getOfferDays() {
        return offerDays;
    }

    public void setOfferDays(List<OfferDay> offerDays) {
        this.offerDays = offerDays;
    }

    public Set<OfferTime> getOfferTimes() {
        return offerTimes;
    }

    public void setOfferTimes(Set<OfferTime> offerTimes) {
        this.offerTimes = offerTimes;
    }

    public Offer addOfferTime(OfferTime offerTime) {
        if ( null == this.offerTimes ) this.offerTimes = new HashSet<>();
        this.offerTimes.add(offerTime);
        return this;
    }
}
