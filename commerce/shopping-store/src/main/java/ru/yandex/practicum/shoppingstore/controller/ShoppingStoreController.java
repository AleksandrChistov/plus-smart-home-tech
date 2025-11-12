package ru.yandex.practicum.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shopping.store.dto.ProductContentDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductRemoveRequestDto;
import ru.yandex.practicum.api.shopping.store.dto.SetProductQuantityStateRequestDto;
import ru.yandex.practicum.api.shopping.store.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.store.enums.QuantityState;
import ru.yandex.practicum.api.shopping.store.service.ShoppingStoreApi;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

@RestController
@RequiredArgsConstructor
@Validated
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ShoppingStoreService shoppingStoreService;

    @Override
    public ProductDto getProductsByCategory(ProductCategory category, Pageable pageable) {
        return shoppingStoreService.getProductsByCategory(category, pageable);
    }

    @Override
    public ProductContentDto getProductById(String productId) {
        return shoppingStoreService.getProductById(productId);
    }

    @Override
    public ProductContentDto createProduct(ProductContentDto productDto) {
        if (productDto.getProductId() != null) {
            throw new IllegalArgumentException("ID товара не должно быть в запросе на создание");
        }
        return shoppingStoreService.createProduct(productDto);
    }

    @Override
    public ProductContentDto updateProduct(ProductContentDto productDto) {
        if (productDto.getProductId() == null || productDto.getProductId().isEmpty()) {
            throw new IllegalArgumentException("ID товара не может быть пустым в запросе на обновление");
        }

        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public boolean setProductQuantityState(String productId, QuantityState quantityState) {
        return shoppingStoreService.setProductQuantityState(
                new SetProductQuantityStateRequestDto(productId, quantityState)
        );
    }

    @Override
    public boolean removeProductFromStore(ProductRemoveRequestDto productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }
}
