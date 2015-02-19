package ru.yesdo.model.data;

import ru.yesdo.model.Merchant;

/**
 * User: Krainov
 * Date: 12.02.2015
 * Time: 18:12
 */
public class UserData {

    private String login;
    private Merchant merchant;
    private boolean partial = true;

    public String getLogin() {
        return login;
    }

    public UserData setLogin(String login) {
        this.login = login;
        return this;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public UserData setMerchant(Merchant merchant) {
        this.merchant = merchant;
        return this;
    }

    public boolean isPartial() {
        return partial;
    }

    public UserData setPartial(boolean partial) {
        this.partial = partial;
        return this;
    }
}
