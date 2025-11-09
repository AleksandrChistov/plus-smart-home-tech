package ru.yandex.practicum.api.shopping.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {
    @NotBlank(message = "ID корзины не может быть пустым")
    String shoppingCartId;
    @NotNull(message = "Список товаров не может отсутствовать")
    Map<String, Integer> products = new HashMap<>();
}
