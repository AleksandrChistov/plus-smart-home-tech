package ru.yandex.practicum.api.payment.service;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.payment.dto.PaymentRequest;

import java.math.BigDecimal;

public interface PaymentApi {
    String URL = "/api/v1/payment";

    @PostMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    PaymentDto payment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping(path = URL + "/totalCost", consumes = MediaType.APPLICATION_JSON_VALUE)
    BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping(path = URL + "/productCost", consumes = MediaType.APPLICATION_JSON_VALUE)
    BigDecimal calculateProductCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping(path = URL + "/refund", consumes = MediaType.APPLICATION_JSON_VALUE)
    void refund(@RequestBody @Valid PaymentRequest request);

    @PostMapping(path = URL + "/failed", consumes = MediaType.APPLICATION_JSON_VALUE)
    void failed(@RequestBody @Valid PaymentRequest request);

}