package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.api.order.service.OrderApi;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    public List<OrderDto> getOrdersByUserName(String username) {
        return orderService.getOrdersByUsername(username);
    }

    @Override
    public OrderDto createNewOrder(@Valid CreateNewOrderRequest request) {
        return orderService.createNewOrder(request);
    }

    @Override
    public OrderDto calculateTotal(@Valid OrderRequest request) {
        return orderService.calculateTotal(request);
    }

    @Override
    public OrderDto calculateDelivery(@Valid OrderRequest request) {
        return orderService.calculateDelivery(request);
    }

    @Override
    public OrderDto assembleOrder(@Valid OrderRequest request) {
        return orderService.assembleOrder(request);
    }

    @Override
    public OrderDto assembleOrderFailed(@Valid OrderRequest request) {
        return orderService.assembleOrderFailed(request);
    }

    @Override
    public OrderDto payOrder(@Valid OrderRequest request) {
        return orderService.payOrder(request);
    }

    @Override
    public OrderDto payOrderFailed(@Valid OrderRequest request) {
        return orderService.payOrderFailed(request);
    }

    @Override
    public OrderDto deliverOrder(@Valid OrderRequest request) {
        return orderService.deliverOrder(request);
    }

    @Override
    public OrderDto deliverOrderFailed(@Valid OrderRequest request) {
        return orderService.deliverOrderFailed(request);
    }

    @Override
    public OrderDto completeOrder(@Valid OrderRequest request) {
        return orderService.completeOrder(request);
    }

    @Override
    public OrderDto returnOrder(@Valid ProductReturnRequest request) {
        return orderService.returnOrder(request);
    }
}

