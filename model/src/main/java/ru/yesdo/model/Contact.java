package ru.yesdo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.fieldaccess.DynamicProperties;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lameroot on 13.12.14.
 * Contact - контактная информация о пользователе или о компании
 * Пока многие параметры здесь указаны чисто приблизительно, поэтому можно использовать только имя и фио
 * Должен хранится в БД
 */
@NodeEntity
@Entity
@Table(name = "contact")
public class Contact {

    public final static String LOCATION_INDEX_NAME = "location_index";

    @Id
    @SequenceGenerator(name = "contact_id_gen", sequenceName = "contact_seq")
    @GeneratedValue(generator = "contact_id_gen", strategy = GenerationType.SEQUENCE)
    @GraphProperty(propertyName = "db_id")
    private Long id;

    @GraphId
    @Column(name = "graph_id")
    private Long graphId;

    @Indexed(indexType = IndexType.POINT, indexName = LOCATION_INDEX_NAME)
    @Column(name = "wkt")
    private String wkt;

    @Type(type = "ru.yesdo.model.DynamicPropertiesType")
    @Column(name = "params")
    private DynamicProperties params = new DynamicPropertiesContainer();

    @GraphProperty(propertyName = "type")
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ContactType type;

    public static enum ContactType {
        OFFER_CONTACT,
        MERCHANT_CONTACT,
        USER_CONTACT,
        PRODUCT_CONTACT
    }

    public Contact addContactParam(String name, Object value, ContactParam.Type type) throws IOException {
        return addContactParam(name, value, type,false);
    }
    public Contact addContactParam(String name, Object value, ContactParam.Type type, boolean isPrivate) throws IOException {
        ContactParam contactParam = new ContactParam(name,value,type,isPrivate);
        params.setProperty(name, JsonUtil.toJson(contactParam));
        return this;
    }
    public Contact addContactParam(ContactParam contactParam) throws IOException {
        params.setProperty(contactParam.getName(), JsonUtil.toJson(contactParam));
        return this;
    }
    public Contact updateContactParam(String name, String newValue) throws IOException{
        ContactParam contactParam = getContactParam(name);
        if ( null == contactParam ) return this;
        contactParam.setValue(newValue);
        params.setProperty(name, JsonUtil.toJson(contactParam));
        return this;
    }
    public Contact removeContactParam(String name) {
        params.removeProperty(name);
        return this;
    }

    @JsonIgnore
    public Set<ContactParam> getContactParams() throws IOException {
        Set<ContactParam> contactParams = new HashSet<>();
        for (Map.Entry<String, Object> entry : params.asMap().entrySet()) {
            String value = (String)entry.getValue();
            ContactParam contactParam = JsonUtil.toObject(ContactParam.class, value);
            contactParams.add(contactParam);
        }
        return contactParams;
    }
    public Set<ContactParam> getContactParams(ContactParam.ContactConditional conditional) throws IOException {
        Set<ContactParam> contactParams = new HashSet<>();
        for (Map.Entry<String, Object> entry : params.asMap().entrySet()) {
            String value = (String)entry.getValue();
            ContactParam contactParam = JsonUtil.toObject(ContactParam.class, value);
            if ( conditional.check(contactParam) ) contactParams.add(contactParam);
        }
        return contactParams;
    }

    public ContactParam getContactParam(String name) throws IOException {
        String value = (String)params.getProperty(name);
        if ( null == value ) return null;
        return JsonUtil.toObject(ContactParam.class, value);
    }


    public Contact setLocation(double lon, double lat) {
        this.wkt = String.format("POINT( %s %s )",lon,lat).replace(",",".");
        return this;
    }

    public String getLocation() {
        return wkt;
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

    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @JsonIgnore
    public DynamicProperties getParams() {
        return params;
    }

    public void setParams(DynamicProperties params) {
        this.params = params;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Contact{");
        sb.append("id=").append(id);
        sb.append(", graphId=").append(graphId);
        sb.append(", wkt='").append(wkt).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
