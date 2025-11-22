package ru.yandex.practicum.api.order.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.api.shared.error.NotFoundException;

import java.util.List;

public interface OrderApi {
    String URL = "/api/v1/order";

    @GetMapping(path = URL, produces = MediaType.APPLICATION_JSON_VALUE)
    List<OrderDto> getOrdersByUserName(@RequestParam @NotNull String username);

    @PutMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping(path = URL + "/calculate/total", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto calculateTotal(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/calculate/delivery", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto calculateDelivery(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/assembly", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto assembleOrder(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/assembly/failed", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto assembleOrderFailed(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto payOrder(@RequestBody @Valid OrderRequest request) throws NotFoundException;

    @PostMapping(path = URL + "/payment/failed", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto payOrderFailed(@RequestBody @Valid OrderRequest request) throws NotFoundException;

    @PostMapping(path = URL + "/delivery", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto deliverOrder(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/delivery/failed", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto deliverOrderFailed(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/completed", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto completeOrder(@RequestBody @Valid OrderRequest request);

    @PostMapping(path = URL + "/return", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request);

}