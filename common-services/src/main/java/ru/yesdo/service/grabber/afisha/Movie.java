package ru.yesdo.service.grabber.afisha;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.LongRange;
import ru.yesdo.model.ContactParam;
import ru.yesdo.model.JsonUtil;
import ru.yesdo.model.Tag;
import ru.yesdo.model.data.ContactData;
import ru.yesdo.model.data.ProductData;

import java.util.Set;

/**
 * Created by lameroot on 08.03.15.
 */
class Movie extends ProductData {

    String director;
    Set<String> actors;
    String creation;
    String siteUrl;
    Set<Tag> tags;
    String description;
    Integer duration;
    LongRange intervalPrices;



    @Override
    public ContactData getContactData() {
        ContactData contact = new ContactData();
        try {
            contact.addContactParam(new ContactParam("MOVIE_DIRECTOR",director, ContactParam.Type.DESCRIPTION));
            contact.addContactParam(new ContactParam("MOVIE_ACTORS", JsonUtil.toJson(actors), ContactParam.Type.DESCRIPTION));
            contact.addContactParam(new ContactParam("MOVIE_CREATION", creation, ContactParam.Type.DESCRIPTION));
            contact.addContactParam(new ContactParam("MOVIE_SITEURL", siteUrl, ContactParam.Type.DESCRIPTION));
            contact.addContactParam(new ContactParam("MOVIE_DESCRIPTION", description, ContactParam.Type.DESCRIPTION));
            contact.addContactParam(new ContactParam("MOVIE_DURATION", duration, ContactParam.Type.DESCRIPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contact;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Movie{");
        sb.append("tile='").append(getTitle()).append('\'');
        sb.append("director='").append(director).append('\'');
        sb.append(", actors=").append(actors);
        sb.append(", creation='").append(creation).append('\'');
        sb.append(", siteUrl='").append(siteUrl).append('\'');
        sb.append(", tags=").append(tags);
        sb.append(", description='").append(description).append('\'');
        sb.append(", duration='").append(duration).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
