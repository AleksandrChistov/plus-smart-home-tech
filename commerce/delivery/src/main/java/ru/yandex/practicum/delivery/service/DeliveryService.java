package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.api.delivery.dto.DeliveryRequest;
import ru.yandex.practicum.api.delivery.service.DeliveryApi;
import ru.yandex.practicum.api.order.dto.OrderDto;

import java.math.BigDecimal;

public interface DeliveryService extends DeliveryApi {

    DeliveryDto create(DeliveryDto deliveryDto);

    BigDecimal calculateTotal(OrderDto orderDto);

    void picked(DeliveryRequest request);

    void successful(DeliveryRequest request);

    void failed(DeliveryRequest request);

}