package ru.yesdo.model;

/**
 * Created by lameroot on 13.12.14.
 * Состояние транзакции
 */
public enum ProcessingOperationState {

    CREATED,
    DEPOSITED,
    REFUNDED,
    REVERSED,
    DECLINED,
    DONE //продукт был доставлен клиенту
}
