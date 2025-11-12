package ru.yandex.practicum.api.shopping.store.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.shopping.store.dto.ProductContentDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductRemoveRequestDto;
import ru.yandex.practicum.api.shopping.store.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.store.enums.QuantityState;

public interface ShoppingStoreApi {
    String URL = "/api/v1/shopping-store";

    @GetMapping(path = URL, produces = MediaType.APPLICATION_JSON_VALUE)
    ProductDto getProductsByCategory(
            @RequestParam("category") @NotNull ProductCategory category,
            @PageableDefault Pageable pageable
    );

    @GetMapping(path = URL + "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ProductContentDto getProductById(@PathVariable @NotBlank String productId);

    @PutMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductContentDto createProduct(@RequestBody @Valid ProductContentDto productDto);

    @PostMapping(path = URL,consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductContentDto updateProduct(@RequestBody @Valid ProductContentDto productDto);

    // Установка статуса по товару. API вызывается со стороны склада.
    @PostMapping(path = URL + "/quantityState")
    boolean setProductQuantityState(
            @RequestParam @NotBlank String productId,
            @RequestParam QuantityState quantityState
    );

    // Удалить товар из ассортимента магазина. Функция для менеджерского состава.
    @PostMapping(path = URL + "/removeProductFromStore", consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean removeProductFromStore(@RequestBody @Valid ProductRemoveRequestDto productId);
}