package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.api.delivery.dto.DeliveryRequest;
import ru.yandex.practicum.api.delivery.service.DeliveryApi;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.delivery.service.DeliveryService;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {
    
    private final DeliveryService deliveryService;
    
    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        return deliveryService.create(deliveryDto);
    }
    
    @Override
    public BigDecimal calculateTotal(OrderDto orderDto) {
        return deliveryService.calculateTotal(orderDto);
    }
    
    @Override
    public void picked(DeliveryRequest request) throws NotFoundException {
        deliveryService.picked(request);
    }
    
    @Override
    public void successful(DeliveryRequest request) throws NotFoundException {
        deliveryService.successful(request);
    }
    
    @Override
    public void failed(DeliveryRequest request) throws NotFoundException {
        deliveryService.failed(request);
    }

}