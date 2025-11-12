package ru.yandex.practicum.shoppingcart.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.shopping.cart.enums.ShoppingCartState;
import ru.yandex.practicum.api.warehouse.service.WarehouseClient;
import ru.yandex.practicum.shoppingcart.dal.dao.ShoppingCartRepository;
import ru.yandex.practicum.shoppingcart.dal.model.ShoppingCart;
import ru.yandex.practicum.shoppingcart.mapper.ShoppingCartMapper;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartMapper shoppingCartMapper;

    private final WarehouseClient warehouseClient;
    
    @Override
    public @Nullable ShoppingCartDto getShoppingCartByUserName(String username) {
        log.info("Получение корзины пользователя {}", username);
        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseGet(() -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setUsername(username);
                    shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
                    return shoppingCartMapper.toDto(shoppingCart);
                });
    }
    
    @Override
    public ShoppingCartDto addProducts(String username, Map<String, Integer> productQuantityMap) {
        log.info("Добавление товаров {} в корзину пользователем {}", productQuantityMap, username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUsername(username);
                    return shoppingCartRepository.saveAndFlush(cart);
                });

        throwIfShoppingCartDeactivated(username, shoppingCart.getCartState());

        shoppingCartMapper.updateModel(productQuantityMap, shoppingCart);

        warehouseClient.checkProducts(shoppingCartMapper.toDto(shoppingCart));

        shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deactivate(String username) {
        log.info("Деактивация корзины пользователя {}", username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Корзина для пользователя " + username + " не найдена"));
        shoppingCart.setCartState(ShoppingCartState.DEACTIVATED);
        shoppingCartRepository.save(shoppingCart);
    }
    
    @Override
    public ShoppingCartDto removeProducts(String username, Set<String> productsIds) {
        log.info("Удаление товаров {} из корзины пользователя {}", productsIds, username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Корзина для пользователя " + username + " не найдена"));

        throwIfShoppingCartDeactivated(username, shoppingCart.getCartState());

        boolean isRemoved = shoppingCart.getProducts().keySet()
                .removeIf(productsIds::contains);

        if (isRemoved) {
            shoppingCartRepository.save(shoppingCart);
        }

        return shoppingCartMapper.toDto(shoppingCart);
    }
    
    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Изменение кол-ва товара {} в корзине пользователя {}", request, username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Корзина для пользователя " + username + " не найдена"));

        throwIfShoppingCartDeactivated(username, shoppingCart.getCartState());

        if (!shoppingCart.getProducts().containsKey(request.getProductId())) {
            throw new NotFoundException("Продукт с ID " + request.getProductId() + " в корзине не найден");
        }

        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());

        warehouseClient.checkProducts(shoppingCartMapper.toDto(shoppingCart));

        shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    private static void throwIfShoppingCartDeactivated(String username, ShoppingCartState shoppingCartState) {
        if (shoppingCartState.equals(ShoppingCartState.DEACTIVATED)) {
            throw new IllegalAccessError("Корзина для пользователя " + username + " не активна");
        }
    }

}