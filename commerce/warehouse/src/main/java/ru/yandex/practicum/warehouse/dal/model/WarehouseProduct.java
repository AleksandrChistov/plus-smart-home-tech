package ru.yandex.practicum.warehouse.dal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private String productId;

    private boolean fragile = false;

    @Column(name = "width", nullable = false)
    private float width;

    @Column(name = "height", nullable = false)
    private float height;

    @Column(name = "depth", nullable = false)
    private float depth;

    @Column(name = "weight", nullable = false)
    private float weight;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @ToString.Exclude
    private WarehouseStock stock;

    public int getQuantity() {
        return stock == null ? 0 : stock.getQuantity();
    }
}