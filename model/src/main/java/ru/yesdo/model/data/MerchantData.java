package ru.yesdo.model.data;

import ru.yesdo.model.Activity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lameroot on 25.01.15.
 */
public class MerchantData {

    private String name;
    private String title;
    private Set<Activity> activities;
    private boolean partial = true;

    public String getName() {
        return name;
    }

    public MerchantData setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MerchantData setTitle(String title) {
        this.title = title;
        return this;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public MerchantData setActivities(Set<Activity> activities) {
        this.activities = activities;
        return this;
    }

    public MerchantData addActivity(Activity activity) {
        if ( null == this.activities ) this.activities = new HashSet<>();
        this.activities.add(activity);
        return this;
    }


    public boolean isPartial() {
        return partial;
    }

    public MerchantData setPartial(boolean partial) {
        this.partial = partial;
        return this;
    }
}
