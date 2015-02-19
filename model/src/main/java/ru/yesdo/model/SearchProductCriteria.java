package ru.yesdo.model;

/**
 * User: Krainov
 * Date: 22.12.2014
 * Time: 18:35
 * Класс для поиска
 */
public class SearchProductCriteria {

    private Double startPrice;
    private Double endPrice;
    private Location location; //тут должен быть класс для выбора конкретной области или то что находится рядом, см. описание функционала языка
    private boolean friends; //пользовались ли друзья данной услугой
    private TimeProduct timeProduct;
    private ProductType typeProduct; //тип услуги
    //искать в области или ещё как

}
