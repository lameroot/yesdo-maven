package ru.yesdo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.fieldaccess.DynamicProperties;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.persistence.Transient;
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
public class Contact {

    public final static String LOCATION_INDEX_NAME = "locations22";

    @GraphId
    private Long id;

    @Indexed(indexType = IndexType.POINT, indexName = LOCATION_INDEX_NAME)
    private String wkt;

    @Transient
    private DynamicProperties params = new DynamicPropertiesContainer();

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


    public void setLocation(double lon, double lat) {
        this.wkt = String.format("POINT( %s %s )",lon,lat).replace(",",".");
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

    @JsonIgnore
    public DynamicProperties getParams() {
        return params;
    }

    public void setParams(DynamicProperties params) {
        this.params = params;
    }


    /*
    private Long id;
    private String firstName;//имя
    private String lastName;//фамилия
    private Set<String> phones;//список телефонов, просто текст через запятую
    private Set<String> emails;//список почтовых адресов, просто через запятую
    private Set<String> skypes;//список скайпов
    private Set<String> socials;//список данных на соцсети
    private Location location;//гео-данные
    private User user;//собственное пользователь, для которого делаем, может быть нулл, если это для мерчанта
    private Merchant merchant;//мерчант, если это контакт для него
    private Map<String,String> params;//список доп/ параметров
    */
}
