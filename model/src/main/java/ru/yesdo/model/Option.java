package ru.yesdo.model;

/**
 * Created by lameroot on 13.12.14.
 * Опции - опции, которыми обладает мерчант.
 * Можно сделать по подобии как сделано в шлюзе
 */
public enum Option {
    LIMIT_ACTIVITY,//так как каждый мерчант может иметь несколько активити, то должно быть ограничение в системных настройках, н-р равное 5. Если
    //данная пермиссия есть, то это ограничение снимается.
    LIMIT_TAG,//такая же ситуация с тэгами
    PRODUCT_AVAILABLE //разшено мерчанту создавать продукты сразу без модерации , если данной пермсиии нет, то продукст создаётся с флагом enable = false

}
