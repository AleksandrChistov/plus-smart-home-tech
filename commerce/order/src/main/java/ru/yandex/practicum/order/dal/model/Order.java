package ru.yandex.practicum.order.dal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.api.order.enums.OrderState;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @Column(name = "order_id")
    @UuidGenerator
    private String orderId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "shopping_cart_id")
    private String shoppingCartId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "delivery_id")
    private String deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private OrderState state = OrderState.NEW;

    @Column(name = "delivery_weight")
    private Float deliveryWeight;

    @Column(name = "delivery_volume")
    private Float deliveryVolume;

    @Column(name = "fragile")
    private Boolean fragile;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @ElementCollection
    @CollectionTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"})
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    private Map<String, Integer> products = new HashMap<>();
}
