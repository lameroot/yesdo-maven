package ru.yesdo.model.data;

import ru.yesdo.model.Activity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lameroot on 25.01.15.
 */
public class ActivityData {

    private String name;
    private String title;
    private Set<Activity> parents;
    private boolean partial = true;

    public String getTitle() {
        return title;
    }

    public ActivityData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActivityData setName(String name) {
        this.name = name;
        return this;
    }

    public Set<Activity> getParents() {
        return parents;
    }

    public ActivityData setParents(Set<Activity> parents) {
        this.parents = parents;
        return this;
    }

    public ActivityData addParent(Activity parent) {
        if ( null == this.parents ) this.parents = new HashSet<>();
        this.parents.add(parent);
        return this;
    }

    public boolean isPartial() {
        return partial;
    }

    public ActivityData setPartial(boolean partial) {
        this.partial = partial;
        return this;
    }
}
