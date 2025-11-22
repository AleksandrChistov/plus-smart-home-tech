package ru.yandex.practicum.api.delivery.service;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.api.delivery.dto.DeliveryRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.shared.error.NotFoundException;

import java.math.BigDecimal;

public interface DeliveryApi {
    String URL = "/api/v1/delivery";

    @PutMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    DeliveryDto create(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping(path = URL + "/cost", consumes = MediaType.APPLICATION_JSON_VALUE)
    BigDecimal calculateTotal(@RequestBody @Valid OrderDto orderDto) throws NotFoundException;

    @PostMapping(path = URL + "/picked", consumes = MediaType.APPLICATION_JSON_VALUE)
    void picked(@RequestBody @Valid DeliveryRequest request) throws NotFoundException;

    @PostMapping(path = URL + "/successful", consumes = MediaType.APPLICATION_JSON_VALUE)
    void successful(@RequestBody @Valid DeliveryRequest request) throws NotFoundException;

    @PostMapping(path = URL + "/failed", consumes = MediaType.APPLICATION_JSON_VALUE)
    void failed(@RequestBody @Valid DeliveryRequest request) throws NotFoundException;

}