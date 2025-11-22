package ru.yandex.practicum.delivery.client.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.warehouse.service.WarehouseApi;
import ru.yandex.practicum.delivery.config.ClientConfig;

@FeignClient(name = "warehouse", configuration = ClientConfig.class, fallback = WarehouseClientFallback.class)
public interface WarehouseClient extends WarehouseApi {
}
