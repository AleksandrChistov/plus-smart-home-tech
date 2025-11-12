package ru.yandex.practicum.shoppingcart.service;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.warehouse.service.WarehouseApi;

@FeignClient(name = "warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient extends WarehouseApi {
}
