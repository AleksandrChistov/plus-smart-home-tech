package ru.yandex.practicum.api.warehouse.service;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseApi {
}
