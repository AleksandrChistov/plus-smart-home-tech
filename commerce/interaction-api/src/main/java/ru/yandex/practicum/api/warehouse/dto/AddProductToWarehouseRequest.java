package ru.yandex.practicum.api.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToWarehouseRequest {
    @NotBlank(message = "ID продукта должен быть указан")
    String productId;
    @NotNull(message = "Количество товара должно быть указано")
    @Min(value = 1, message = "Количество товара не может быть меньше 1")
    Integer quantity;
}
