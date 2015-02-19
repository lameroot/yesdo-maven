package ru.yesdo.model.data;

import ru.yesdo.model.Merchant;

import java.util.Date;

/**
 * Created by lameroot on 28.01.15.
 */
public class ProductData {

    private String title;
    private Merchant merchant;
    private Date createdAt;
    private boolean enabled;
    private String code;
    private boolean partial = true;

    //add user, raitings, friends


    public String getTitle() {
        return title;
    }

    public ProductData setTitle(String title) {
        this.title = title;
        return this;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public ProductData setMerchant(Merchant merchant) {
        this.merchant = merchant;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ProductData setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ProductData setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isPartial() {
        return partial;
    }

    public ProductData setPartial(boolean partial) {
        this.partial = partial;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ProductData setCode(String code) {
        this.code = code;
        return this;
    }
}
