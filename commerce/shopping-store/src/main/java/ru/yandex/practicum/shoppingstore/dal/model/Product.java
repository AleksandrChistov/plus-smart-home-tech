package ru.yandex.practicum.shoppingstore.dal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.api.shopping.cart.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.cart.enums.ProductState;
import ru.yandex.practicum.api.shopping.cart.enums.QuantityState;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString
public class Product {
    @Id
    @Column(name = "product_id")
    @UuidGenerator
    private String productId;

    @NotBlank
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotBlank
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_src")
    private String imageSrc;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    private QuantityState quantityState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    private ProductState productState = ProductState.ACTIVE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategory productCategory;

    @NotNull
    @Column(name = "price", nullable = false)
    private BigDecimal price;
}