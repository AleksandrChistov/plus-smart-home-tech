package ru.yandex.practicum.warehouse.dal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "warehouse_stock")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStock {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private int quantity = 0;
}