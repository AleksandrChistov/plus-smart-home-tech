package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.payment.dto.PaymentRequest;
import ru.yandex.practicum.api.payment.service.PaymentApi;

import java.math.BigDecimal;

public interface PaymentService extends PaymentApi {
    /**
     * Формирование оплаты для заказа
     */
    PaymentDto payment(OrderDto orderDto);

    /**
     * Расчёт стоимости товаров в заказе
     */
    BigDecimal calculateProductCost(OrderDto orderDto);

    /**
     * Расчёт полной стоимости заказа, включая стоимость товаров, налога и доставки
     */
    BigDecimal calculateTotalCost(OrderDto orderDto);

    /**
     * Эмуляция успешной оплаты платежного шлюза
     */
    void refund(PaymentRequest request);

    /**
     * Эмуляция отказа в оплате платежного шлюза
     */
    void failed(PaymentRequest request);
}