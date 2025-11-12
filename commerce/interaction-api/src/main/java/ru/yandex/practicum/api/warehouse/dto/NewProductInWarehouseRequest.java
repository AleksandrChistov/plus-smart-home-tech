package ru.yandex.practicum.api.warehouse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewProductInWarehouseRequest {
    @NotBlank(message = "ID продукта должен быть указан")
    String productId;
    Boolean fragile;
    @NotNull(message = "Габариты товара должны быть указаны")
    @Valid DimensionDto dimension;
    @NotNull(message = "Вес товара должен быть указан")
    @Min(value = 1, message = "Вес товара не может быть меньше 1 кг")
    Float weight;
}
