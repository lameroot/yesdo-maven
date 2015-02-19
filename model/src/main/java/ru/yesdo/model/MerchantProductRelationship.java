package ru.yesdo.model;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lameroot on 28.01.15.
 */
@RelationshipEntity(type = "PRODUCT")
//@Entity
//@Table(name = "product_params")
public class MerchantProductRelationship {

    @GraphId
    @Id
    @SequenceGenerator(name = "product_params_id_gen", sequenceName = "product_params_seq")
    @GeneratedValue(generator = "product_params_id_gen", strategy = GenerationType.SEQUENCE)
    private Long id;

    @StartNode
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_merchant_id",nullable = true)
    private Merchant merchant;

    @EndNode
    @OneToOne
    @JoinColumn(name = "fk_product_id")
    private Product product;

    private Long amount;
    private boolean enabled;//доступен или нет.
    @Enumerated(EnumType.STRING)
    private Publicity publicity;//публичность данного продукта, он может быть скрытый, может быть приватный, публичный, только для избранных
    @Transient
    private TimeProduct timeProduct;//время в которое можно воспользоваться услугой
    @Enumerated
    private ProductType productType;//тип продукта
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_at")
    private Date expirationAt;//дата истечения возможности использования продуктом

    public MerchantProductRelationship(){}
    public MerchantProductRelationship(Merchant merchant, Product product, Long amount) {
        this.merchant = merchant;
        this.product = product;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
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
}
