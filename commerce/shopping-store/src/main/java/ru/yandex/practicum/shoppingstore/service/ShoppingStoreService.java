package ru.yandex.practicum.shoppingstore.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.api.shopping.cart.dto.ProductDto;
import ru.yandex.practicum.api.shopping.cart.dto.ProductRemoveRequestDto;
import ru.yandex.practicum.api.shopping.cart.dto.SetProductQuantityStateRequestDto;
import ru.yandex.practicum.api.shopping.cart.enums.ProductCategory;

import java.util.List;

public interface ShoppingStoreService {

    List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto getProductById(String productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean setProductQuantityState(SetProductQuantityStateRequestDto request);

    boolean removeProductFromStore(ProductRemoveRequestDto request);

}
