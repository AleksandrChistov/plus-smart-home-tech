package ru.yandex.practicum.api.shopping.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.api.shopping.store.enums.QuantityState;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SetProductQuantityStateRequestDto {
    @NotBlank(message = "ID товара должен быть заполнен")
    String productId;
    @NotNull(message = "Состояние остатка должно быть заполнено")
    QuantityState quantityState;
}
