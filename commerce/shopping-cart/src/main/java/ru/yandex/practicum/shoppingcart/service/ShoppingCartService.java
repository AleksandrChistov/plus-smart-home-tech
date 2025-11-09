package ru.yandex.practicum.shoppingcart.service;

import ru.yandex.practicum.api.shopping.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;

import java.util.Map;
import java.util.Set;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCartByUserName(String username);

    ShoppingCartDto addProducts(String username, Map<String, Integer> productQuantityMap);

    void deactivate(String username);

    ShoppingCartDto removeProducts(String username, Set<String> productsIds);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);

}