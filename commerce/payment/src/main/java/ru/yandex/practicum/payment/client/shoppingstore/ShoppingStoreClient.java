package ru.yandex.practicum.payment.client.shoppingstore;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.shopping.store.service.ShoppingStoreApi;
import ru.yandex.practicum.payment.config.ShoppingStoreClientConfig;

@FeignClient(name = "shopping-store", configuration = ShoppingStoreClientConfig.class, fallback = ShoppingStoreClientFallback.class)
public interface ShoppingStoreClient extends ShoppingStoreApi {
}