package ru.yandex.practicum.shoppingcart.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shoppingcart.dal.model.ShoppingCart;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, String> {

    Optional<ShoppingCart> findByUsername(String username);

}
