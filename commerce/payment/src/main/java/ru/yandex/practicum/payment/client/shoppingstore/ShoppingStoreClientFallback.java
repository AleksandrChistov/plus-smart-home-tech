package ru.yandex.practicum.payment.client.shoppingstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.shared.error.ServiceUnavailableException;
import ru.yandex.practicum.api.shopping.store.dto.ProductContentDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductRemoveRequestDto;
import ru.yandex.practicum.api.shopping.store.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.store.enums.QuantityState;

@Slf4j
@Component
public class ShoppingStoreClientFallback implements ShoppingStoreClient {
    @Override
    public ProductDto getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.error("Товары {} не были получены", category);
        throw new ServiceUnavailableException("ShoppingStoreClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public ProductContentDto getProductById(String productId) {
        log.error("Товар {} не был получен", productId);
        throw new ServiceUnavailableException("ShoppingStoreClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public ProductContentDto createProduct(ProductContentDto productDto) {
        log.error("Товар {} не был создан", productDto);
        throw new ServiceUnavailableException("ShoppingStoreClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public ProductContentDto updateProduct(ProductContentDto productDto) {
        log.error("Товар {} не был обновлен", productDto);
        throw new ServiceUnavailableException("ShoppingStoreClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public boolean setProductQuantityState(String productId, QuantityState quantityState) {
        log.error("Кол-во {} товара {} не было обновлено", productId, quantityState);
        throw new ServiceUnavailableException("ShoppingStoreClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public boolean removeProductFromStore(ProductRemoveRequestDto productId) {
        log.error("Товар {} не был удален", productId);
        throw new ServiceUnavailableException("ShoppingStoreClient временно недоступен. Попробуйте позже.");
    }
}
