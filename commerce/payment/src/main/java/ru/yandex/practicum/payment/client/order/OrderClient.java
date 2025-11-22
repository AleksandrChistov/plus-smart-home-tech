package ru.yandex.practicum.payment.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.order.service.OrderApi;
import ru.yandex.practicum.payment.config.ClientConfig;

@FeignClient(name = "order", configuration = ClientConfig.class, fallback = OrderClientFallback.class)
public interface OrderClient extends OrderApi {
}