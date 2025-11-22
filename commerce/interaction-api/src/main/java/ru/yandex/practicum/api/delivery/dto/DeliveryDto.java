package ru.yandex.practicum.api.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.yandex.practicum.api.delivery.enums.DeliveryState;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {
    @NotBlank(message = "ID доставки не может быть пустым")
    String deliveryId;
    @NotBlank(message = "ID заказа не может быть пустым")
    String orderId;
    DeliveryState deliveryState = DeliveryState.CREATED;
    @NotBlank(message = "Адрес отправки не может быть пустым")
    AddressDto fromAddress;
    @NotBlank(message = "Адрес доставки не может быть пустым")
    AddressDto toAddress;
}
