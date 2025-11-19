package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.dal.dao.OrderRepository;
import ru.yandex.practicum.order.mapper.OrderMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getOrdersByUsername(String username) {
        return orderMapper.toDtoList(orderRepository.findByUsername(username));
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        return null;
    }

    @Override
    public OrderDto calculateTotal(OrderRequest request) {
        // todo: Добавить Feign-клиенты
        //  delivery:
        //    - `deliveryCost` для **расчёта стоимости доставки** при общем расчёте стоимости.
        //  payment`:
        //    - `productCost` для **расчёта стоимости товаров**;
        //    - `getTotalCost` для **расчёта общей стоимости** товаров, доставки и налога;
        return null;
    }

    @Override
    public OrderDto calculateDelivery(OrderRequest request) {
        // todo: Добавить Feign-клиент warehouse:
        //    - `getWarehouseAddress` для формирования **адреса** «Откуда», чтобы рассчитать и сохранить «Доставку» (**идентификатор**).
        return null;
    }

    @Override
    public OrderDto assembleOrder(OrderRequest request) {
        // todo: Добавить Feign-клиент warehouse:
        //    - `assemblyProductForOrderFromShoppingCart` для **сбора заказа** по продуктовой корзине;
        return null;
    }

    @Override
    public OrderDto assembleOrderFailed(OrderRequest request) {
        return null;
    }

    @Override
    public OrderDto payOrder(OrderRequest request) {
        // todo: Добавить Feign-клиент payment:
        //    - `payment` для **запуска** процесса **оплаты**.
        return null;
    }

    @Override
    public OrderDto payOrderFailed(OrderRequest request) {
        return null;
    }

    @Override
    public OrderDto deliverOrder(OrderRequest request) {
        // todo: Добавить Feign-клиент delivery:
        //    - `planDelivery` для **создания доставки**;
        return null;
    }

    @Override
    public OrderDto deliverOrderFailed(OrderRequest request) {
        return null;
    }

    @Override
    public OrderDto completeOrder(OrderRequest request) {
        return null;
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        return null;
    }
}
