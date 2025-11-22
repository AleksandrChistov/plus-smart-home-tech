package ru.yandex.practicum.api.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.api.order.enums.OrderState;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    @NotBlank(message = "ID заказа не может быть пустым")
    String orderId;
    String shoppingCartId;
    @NotNull(message = "Список товаров не может отсутствовать")
    @Builder.Default
    Map<String, Integer> products = new HashMap<>();
    String paymentId;
    String deliveryId;
    @NotNull(message = "Состояние заказа должно быть указано")
    @Builder.Default
    OrderState state = OrderState.NEW;
    Float deliveryWeight;
    Float deliveryVolume;
    Boolean fragile;
    BigDecimal productPrice;
    BigDecimal deliveryPrice;
    BigDecimal totalPrice;
}
