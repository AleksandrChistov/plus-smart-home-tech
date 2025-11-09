package ru.yandex.practicum.shoppingcart.dal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.api.shopping.cart.enums.ShoppingCartState;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@ToString
public class ShoppingCart {
    @Id
    @Column(name = "cart_id")
    @UuidGenerator
    private String shoppingCartId;

    @NotBlank
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "cart_state", nullable = false)
    private ShoppingCartState cartState = ShoppingCartState.ACTIVE;

    @ElementCollection
    @CollectionTable(
            name = "shopping_carts_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<String, Integer> products = new HashMap<>();

}
