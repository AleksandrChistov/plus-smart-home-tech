package ru.yandex.practicum.order.service;

import ru.yandex.practicum.api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.order.dto.ProductReturnRequest;

import java.util.List;

public interface OrderService {

    List<OrderDto> getOrdersByUsername(String username);

    OrderDto createNewOrder(CreateNewOrderRequest request);

    OrderDto calculateTotal(OrderRequest request);

    OrderDto calculateDelivery(OrderRequest request);

    OrderDto assembleOrder(OrderRequest request);

    OrderDto assembleOrderFailed(OrderRequest request);

    OrderDto payOrder(OrderRequest request);

    OrderDto payOrderFailed(OrderRequest request);

    OrderDto deliverOrder(OrderRequest request);

    OrderDto deliverOrderFailed(OrderRequest request);

    OrderDto completeOrder(OrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

}
