package ru.yandex.practicum.api.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedProductsDto {
    @NotNull(message = "Общий вес доставки должен быть указан")
    Float deliveryWeight;
    @NotNull(message = "Общий объем доставки должен быть указан")
    Float deliveryVolume;
    @NotNull(message = "Укажите есть ли хрупкие вещи в доставке")
    Boolean fragile;
}
