package ru.yandex.practicum.api.shopping.cart.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.shopping.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;

import java.util.Map;
import java.util.Set;

import static ru.yandex.practicum.api.shopping.cart.service.ShoppingCartApi.URL;

@RequestMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
public interface ShoppingCartApi {
    String URL = "/api/v1/shopping-cart";

    @GetMapping
    ShoppingCartDto getShoppingCartByUserName(@RequestParam @NotNull String username);

    @PutMapping
    ShoppingCartDto addProducts(
            @RequestParam @NotNull String username,
            @RequestBody @Valid Map<String, Integer> productQuantityMap
    );

    /**
    * Деактивация корзины товаров для пользователя.
     */
    @DeleteMapping
    void deactivate(@RequestParam @NotNull String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProducts(
            @RequestParam @NotNull String username,
            @RequestBody @Valid Set<String> productsIds
    );

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(
            @RequestParam @NotNull String username,
            @RequestBody @Valid ChangeProductQuantityRequest request
    );
}