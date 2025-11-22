package ru.yandex.practicum.payment.client.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.api.shared.error.ServiceUnavailableException;

import java.util.List;

@Slf4j
@Component
public class OrderClientFallback implements OrderClient {
    @Override
    public List<OrderDto> getOrdersByUserName(String username) {
        log.error("Заказы пользователя {} не были получены", username);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.error("Новый заказ не был создан {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto calculateTotal(OrderRequest request) {
        log.error("Стоимость заказа не была рассчитана {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto calculateDelivery(OrderRequest request) {
        log.error("Стоимость доставки не была рассчитана {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto assembleOrder(OrderRequest request) {
        log.error("Заказ не был собран {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto assembleOrderFailed(OrderRequest request) {
        log.error("Статус заказа не был изменен на PAYMENT_FAILED {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto payOrder(OrderRequest request) {
        log.error("Статус заказа не был изменен на PAID {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto payOrderFailed(OrderRequest request) {
        log.error("Статус заказа не был изменен на PAYMENT_FAILED {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto deliverOrder(OrderRequest request) {
        log.error("Статус заказа не был изменен на ON_DELIVERY {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto deliverOrderFailed(OrderRequest request) {
        log.error("Статус заказа не был изменен на DELIVERY_FAILED {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto completeOrder(OrderRequest request) {
        log.error("Статус заказа не был изменен на COMPLETED {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.error("Заказ не был возвращен {}", request);
        throw new ServiceUnavailableException("OrderClient временно недоступен. Попробуйте позже.");
    }
}
