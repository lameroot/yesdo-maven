package ru.yesdo.model;

import java.util.Date;

/**
 * Created by lameroot on 13.12.14.
 * Класс для проведения транзакционной модели
 */
public class ProcessingOperation {

    private Long id;
    private Merchant merchant;//мерчант для которого производится транзакция
    private User user;//если известен, то пишется тут
    private Integer amount;//сумма на котороую проивзрдится транзакция
    private Product product;//сам продук мерчанта, по которому производится транзакция
    private Date createdAt;//дата создания
    private PaymentData paymentData;//плат>жные данные
    private ProcessingOperationState state;//статус транзакции
    private Integer fee;//комиссия
    private ProcessingType processingType;//тип операции
    private Date productAt;//к какому времени должен быть предоставлен продукт/услуга пользователю
}
