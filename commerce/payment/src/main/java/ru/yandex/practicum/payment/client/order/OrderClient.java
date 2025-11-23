package ru.yandex.practicum.payment.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.order.service.OrderApi;
import ru.yandex.practicum.payment.config.OrderClientConfig;

@FeignClient(name = "order", configuration = OrderClientConfig.class, fallback = OrderClientFallback.class)
public interface OrderClient extends OrderApi {
}