package ru.yandex.practicum.api.shopping.cart.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.shopping.cart.dto.*;
import ru.yandex.practicum.api.shopping.cart.enums.ProductCategory;

import java.util.List;

import static ru.yandex.practicum.api.shopping.cart.service.ShoppingStoreApi.URL;

@RequestMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
public interface ShoppingStoreApi {
    String URL = "/api/v1/shopping-store";

    @GetMapping
    List<ProductDto> getProductsByCategory(
            @RequestParam("category") @NotNull ProductCategory category,
            @PageableDefault Pageable pageable
    );

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable @NotBlank String productId);

    @PutMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    // Установка статуса по товару. API вызывается со стороны склада.
    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityStateRequestDto request);

    // Удалить товар из ассортимента магазина. Функция для менеджерского состава.
    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody @Valid ProductRemoveRequestDto productId);
}