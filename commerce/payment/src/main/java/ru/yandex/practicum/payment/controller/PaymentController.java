package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.payment.dto.PaymentRequest;
import ru.yandex.practicum.api.payment.service.PaymentApi;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @Override
    public PaymentDto payment(@Valid OrderDto orderDto) {
        return paymentService.payment(orderDto);
    }

    @Override
    public BigDecimal calculateTotalCost(@Valid OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    public BigDecimal calculateProductCost(@Valid OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @Override
    public void refund(@Valid PaymentRequest request) {
        paymentService.refund(request);
    }

    @Override
    public void failed(@Valid PaymentRequest request) {
        paymentService.failed(request);
    }
}