package ru.yandex.practicum.api.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShippedToDeliveryRequest {
    @NotBlank(message = "ID заказа должен быть указан")
    String orderId;
    @NotBlank(message = "ID доставки должен быть указан")
    String deliveryId;
}
