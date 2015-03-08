package ru.yesdo.model.data;

import ru.yesdo.model.Contact;
import ru.yesdo.model.ContactParam;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Krainov
 * Date: 13.02.2015
 * Time: 18:09
 */
public class ContactData {

    private Set<ContactParam> contactParams = new HashSet<>();
    private double lon, lat;
    private Contact.ContactType type;

    public ContactData addContactParam(ContactParam... contactParams) {
        for (ContactParam contactParam : contactParams) {
            this.contactParams.add(contactParam);
        }
        return this;
    }

    public ContactData setLocation(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        return this;
    }

    public Set<ContactParam> getContactParams() {
        return contactParams;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public Contact.ContactType getType() {
        return type;
    }

    public void setType(Contact.ContactType type) {
        this.type = type;
    }

    public Contact toContact() {
        Contact contact = new Contact();
        contact.setLocation(getLon(),getLat());

        try {
            for (ContactParam contactParam : getContactParams()) {
                contact.addContactParam(contactParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        contact.setType(getType());
        return contact;
    }
}
