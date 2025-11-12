package ru.yandex.practicum.api.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDto {
    @NotNull(message = "Ширина товара должна быть указана")
    @Min(value = 1, message = "Ширина товара не может быть меньше 1")
    Float width;
    @NotNull(message = "Высота товара должна быть указана")
    @Min(value = 1, message = "Высота товара не может быть меньше 1")
    Float height;
    @NotNull(message = "Глубина товара должна быть указана")
    @Min(value = 1, message = "Глубина товара не может быть меньше 1")
    Float depth;
}
