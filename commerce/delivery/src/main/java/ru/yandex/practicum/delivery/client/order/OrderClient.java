package ru.yandex.practicum.delivery.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.order.service.OrderApi;
import ru.yandex.practicum.delivery.config.ClientConfig;

@FeignClient(name = "order", configuration = ClientConfig.class, fallback = OrderClientFallback.class)
public interface OrderClient extends OrderApi {
}