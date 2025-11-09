package ru.yandex.practicum.shoppingcart.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shopping.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.shopping.cart.service.ShoppingCartApi;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartApi {
    
    private final ShoppingCartService shoppingCartService;

    @Override
    public @Nullable ShoppingCartDto getShoppingCartByUserName(String username) {
        return shoppingCartService.getShoppingCartByUserName(username);
    }

    @Override
    public ShoppingCartDto addProducts(String username, Map<String, Integer> productQuantityMap) {
        return shoppingCartService.addProducts(username, productQuantityMap);
    }

    @Override
    public void deactivate(String username) {
        shoppingCartService.deactivate(username);
    }

    @Override
    public ShoppingCartDto removeProducts(String username, Set<String> productsIds) {
        return shoppingCartService.removeProducts(username, productsIds);
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        return shoppingCartService.changeQuantity(username, request);
    }
}