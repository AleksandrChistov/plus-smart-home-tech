package ru.yandex.practicum.shoppingstore.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.api.shopping.store.dto.ProductContentDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductRemoveRequestDto;
import ru.yandex.practicum.api.shopping.store.dto.SetProductQuantityStateRequestDto;
import ru.yandex.practicum.api.shopping.store.enums.ProductCategory;

public interface ShoppingStoreService {

    ProductDto getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductContentDto getProductById(String productId);

    ProductContentDto createProduct(ProductContentDto productDto);

    ProductContentDto updateProduct(ProductContentDto productDto);

    boolean setProductQuantityState(SetProductQuantityStateRequestDto request);

    boolean removeProductFromStore(ProductRemoveRequestDto request);

}
