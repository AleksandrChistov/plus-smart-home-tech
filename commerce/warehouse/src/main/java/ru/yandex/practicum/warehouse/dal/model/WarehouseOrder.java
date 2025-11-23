package ru.yandex.practicum.warehouse.dal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "warehouse_orders")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseOrder {
    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "delivery_id", unique = true)
    private String deliveryId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "warehouse_orders_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @Column(name = "product_id")
    @ToString.Exclude
    @Builder.Default
    private Set<String> productIds = new HashSet<>();
}