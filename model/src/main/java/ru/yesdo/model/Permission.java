package ru.yesdo.model;

/**
 * Created by lameroot on 13.12.14.
 * Список пермиссий, которые есть у пользователя, их выдаёт тот, кто создал его
 */
public enum Permission {
    MANAGE_USERS,
    MANAGE_MERCHANTS,
    MANAGE_ACTIVITIES,
    MANAGE_PRODUCTS,
    MANAGE_TAGS,
    MANAGE_EVENTS,
    LIST_STATISTICS,
    LIST_TRANSACTIONS,
    REVERSE_PERMISSION,
    APPLICATION_ADMIN,
    REMOVE_PRODUCT,
    LIST_USER_ACTIONS

}
