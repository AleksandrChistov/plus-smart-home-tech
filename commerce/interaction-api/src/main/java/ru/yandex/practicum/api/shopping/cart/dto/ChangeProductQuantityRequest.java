package ru.yandex.practicum.api.shopping.cart.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProductQuantityRequest {
    @NotBlank(message = "ID товара должен быть указан")
    String productId;
    @NotNull(message = "Новое количество товара должно быть указано")
    @Min(value = 0, message = "Количество не может быть меньше 0")
    Integer newQuantity;
}
