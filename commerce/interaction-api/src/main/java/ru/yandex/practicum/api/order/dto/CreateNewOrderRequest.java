package ru.yandex.practicum.api.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewOrderRequest {
    @NotNull(message = "Данные корзины должны быть заполнены")
    ShoppingCartDto shoppingCart;
    @NotNull(message = "Адрес доставки должен быть указан")
    AddressDto deliveryAddress;
}
