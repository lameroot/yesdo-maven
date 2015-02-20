package ru.yesdo.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lameroot on 13.12.14.
 * Продукт который предоставляет мерчант и за которую платит компания
 * В админке должна быть вкладка, которую видит мерчант, то есть у него это одна вкладка, но если вход произведён н-р администратором,
 * то должен быть возможен переход от продуктов одной компании к продуктам другой.
 * Также продукт могу создавать не только мерчанты, но и сами пользователи на сайте, то должна быть также вьюшка для отображения
 * продуктов, которые создал сам пользователь (!!!на данном этапе это делать не надо, но можно предусмотреть как это будет выглядеть)
 * Также так как мерчанты сами могут создавать продукты, то могут насоздавать всякого чего нельзя выпускать на сайт, поэтому должна быть возможность
 * пост-модерации продуктов. Должна быть пермиссия для мерчанта, что если она у него есть, то можно создавать продукты без
 * пост-модерации, но если нет, то админу (или контен-мэнеджеру) отправляется уведомление, что надо произвести модерацию новых продуктов.
 * Это должна быть отдельная панель для админа, где он просто видит список всех новых продуктов (лучше разграничить по мерчантам),
 * где можно увидеть что за продукт, что за медиа используются, что за текст, цена и др, если всё удовлетворяет, то админ производит конфирм и продукт попадает на витрину
 * мерчанта, иначе должна быть возможность отправить уведомление создателю, с текстом почему данный продукт не удовлетворяет политике сайта.
 * Также предусмотреть возможность на этой же странице видеть продукты, которые создают сами пользователи, должна быть инфа кто и что создал. (!!!данный функйиона можно только предусмотреть но не делать)
 */
@NodeEntity
@Entity
@Table(name = "product")
public class Product {

    public final static String INDEX_FOR_CODE = "product_code";

    @Id
    @SequenceGenerator(name = "product_id_gen", sequenceName = "product_seq")
    @GeneratedValue(generator = "product_id_gen", strategy = GenerationType.SEQUENCE)
    @GraphProperty(propertyName = "db_id")
    private Long id;

    @GraphId
    @Column(name = "graph_id")
    private Long graphId;

    @GraphProperty
    private String title;//название продукта

    @GraphProperty
    @Column(name = "code", nullable = false, unique = true)
    @Indexed(indexName = INDEX_FOR_CODE, indexType = IndexType.SIMPLE, unique = true)
    private String code;

    @RelatedTo(direction = Direction.INCOMING, type = "PRODUCT")
    @ManyToOne
    @JoinColumn(name = "fk_merchant_id",nullable = false)
    private Merchant merchant;//мерчант, кому пренадлежит данная услуга

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;//дата создания

    @Column(name = "enabled")
    private boolean enabled;


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


    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Product{");
        sb.append("id=").append(id);
        sb.append(", graphId=").append(graphId);
        sb.append(", title='").append(title).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", enabled=").append(enabled);
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        return sb.toString();
    }

    //    private Blog description;//краткое описание продукта
//    private Set<Media> medias;//список картинок или видео для данного продукта
//    private Location location;//где находится данный продукт или услуга
//    private Set<Tag> tags;//список тэгов, кол-во должно ограничиваться пермиссией, если это делает мерчант
//    private Set<User> users;//список пользоватлей, кто воспользовался данной услугой или в случае с пользователем, кто просмотрел или лайкнул данный продукт
}
