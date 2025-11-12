package ru.yandex.practicum.api.shopping.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    @NotNull(message = "Список товаров должен быть указан")
    List<ProductContentDto> content;
    List<SortDto> sort;
}
