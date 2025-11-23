package ru.yandex.practicum.order.client.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.warehouse.service.WarehouseApi;
import ru.yandex.practicum.order.config.ClientConfig;

@FeignClient(name = "warehouse", configuration = ClientConfig.class, fallback = WarehouseClientFallback.class)
public interface WarehouseClient extends WarehouseApi {
}
