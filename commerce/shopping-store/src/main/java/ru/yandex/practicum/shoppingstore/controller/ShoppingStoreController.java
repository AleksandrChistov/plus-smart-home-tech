package ru.yandex.practicum.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shopping.cart.dto.*;
import ru.yandex.practicum.api.shopping.cart.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.cart.service.ShoppingStoreApi;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ShoppingStoreService shoppingStoreService;

    @Override
    public List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return shoppingStoreService.getProductsByCategory(category, pageable);
    }

    @Override
    public ProductDto getProductById(String productId) {
        return shoppingStoreService.getProductById(productId);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (productDto.getProductId() != null) {
            throw new IllegalArgumentException("ID товара не должно быть в запросе на создание");
        }
        return shoppingStoreService.createProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getProductId() == null || productDto.getProductId().isEmpty()) {
            throw new IllegalArgumentException("ID товара не может быть пустым в запросе на обновление");
        }

        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequestDto request) {
        return shoppingStoreService.setProductQuantityState(request);
    }

    @Override
    public boolean removeProductFromStore(ProductRemoveRequestDto productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }
}
