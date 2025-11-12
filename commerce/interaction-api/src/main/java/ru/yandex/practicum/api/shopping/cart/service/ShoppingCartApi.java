package ru.yandex.practicum.api.shopping.cart.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.shopping.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;

import java.util.Map;
import java.util.Set;

public interface ShoppingCartApi {
    String URL = "/api/v1/shopping-cart";

    @GetMapping(path = URL, produces = MediaType.APPLICATION_JSON_VALUE)
    ShoppingCartDto getShoppingCartByUserName(@RequestParam @NotNull String username);

    @PutMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    ShoppingCartDto addProducts(
            @RequestParam @NotNull String username,
            @RequestBody @Valid Map<String, Integer> productQuantityMap
    );

    /**
    * Деактивация корзины товаров для пользователя.
     */
    @DeleteMapping(path = URL)
    void deactivate(@RequestParam @NotNull String username);

    @PostMapping(path = URL + "/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    ShoppingCartDto removeProducts(
            @RequestParam @NotNull String username,
            @RequestBody @Valid Set<String> productsIds
    );

    @PostMapping(path = URL + "/change-quantity", consumes = MediaType.APPLICATION_JSON_VALUE)
    ShoppingCartDto changeQuantity(
            @RequestParam @NotNull String username,
            @RequestBody @Valid ChangeProductQuantityRequest request
    );
}