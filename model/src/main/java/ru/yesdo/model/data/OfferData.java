package ru.yesdo.model.data;

import ru.yesdo.model.*;

import java.util.Date;

/**
 * Created by lameroot on 11.02.15.
 */
public class OfferData {

    private Long amount;
    private boolean enabled;//доступен или нет.
    private Publicity publicity;//публичность данного продукта, он может быть скрытый, может быть приватный, публичный, только для избранных
    private TimeProduct timeProduct;//время в которое можно воспользоваться услугой
    private ProductType productType;//тип продукта
    private Date expirationAt;//
    private ContactData contactData;


    public Offer toOffer() {
        Offer offer = new Offer();
        offer.setEnabled(isEnabled());
        offer.setAmount(getAmount());
        offer.setExpirationAt(getExpirationAt());
        offer.setProductType(getProductType());
        offer.setPublicity(getPublicity());
        offer.setTimeProduct(getTimeProduct());


        if ( null != contactData ) {
            OfferContact offerContact = new OfferContact();
            //offerContact.setOffer(offer);
            offerContact.setLocation(contactData.getLon(),contactData.getLat());

            try {
                for (ContactParam contactParam : contactData.getContactParams()) {
                    offerContact.addContactParam(contactParam);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            offer.setContact(offerContact);
        }


        return offer;
    }

    public Long getAmount() {
        return amount;
    }

    public OfferData setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public OfferData setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Publicity getPublicity() {
        return publicity;
    }

    public OfferData setPublicity(Publicity publicity) {
        this.publicity = publicity;
        return this;
    }

    public TimeProduct getTimeProduct() {
        return timeProduct;
    }

    public OfferData setTimeProduct(TimeProduct timeProduct) {
        this.timeProduct = timeProduct;
        return this;
    }

    public ProductType getProductType() {
        return productType;
    }

    public OfferData setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    public Date getExpirationAt() {
        return expirationAt;
    }

    public void setExpirationAt(Date expirationAt) {
        this.expirationAt = expirationAt;
    }

    public ContactData getContactData() {
        return contactData;
    }

    public OfferData setContactData(ContactData contactData) {
        this.contactData = contactData;
        return this;
    }
}
