package ru.yandex.practicum.order.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.delivery.service.DeliveryApi;
import ru.yandex.practicum.order.config.ClientConfig;

@FeignClient(name = "delivery", configuration = ClientConfig.class, fallback = DeliveryClientFallback.class)
public interface DeliveryClient extends DeliveryApi {
}