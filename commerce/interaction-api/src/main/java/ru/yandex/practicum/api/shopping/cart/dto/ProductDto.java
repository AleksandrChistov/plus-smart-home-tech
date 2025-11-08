package ru.yandex.practicum.api.shopping.cart.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.api.shopping.cart.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.cart.enums.ProductState;
import ru.yandex.practicum.api.shopping.cart.enums.QuantityState;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    String productId;
    @NotBlank(message = "Название товара должно быть заполнено")
    String productName;
    @NotBlank(message = "Описание товара должно быть заполнено")
    String description;
    String imageSrc;
    @NotNull(message = "Состояние остатка должно быть заполнено")
    QuantityState quantityState;
    @NotNull(message = "Состояние товара должно быть заполнено")
    ProductState productState;
    @NotNull(message = "Категория товара должна быть заполнена")
    ProductCategory productCategory;
    @NotNull(message = "Цена товара должна быть указана")
    @DecimalMin(value = "1.00", message = "Цена не может быть меньше 1 руб.")
    @Digits(integer = 17, fraction = 2, message = "В цене поддерживается максимум 17 цифр и 2 после запятой")
    BigDecimal price;
}
