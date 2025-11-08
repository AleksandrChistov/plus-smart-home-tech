package ru.yandex.practicum.api.shopping.cart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductRemoveRequestDto {
    @NotBlank(message = "ID товара должен быть указан")
    String productId;
}
