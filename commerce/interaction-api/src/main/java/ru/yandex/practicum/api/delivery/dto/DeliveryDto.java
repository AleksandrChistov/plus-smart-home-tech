package ru.yandex.practicum.api.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.api.delivery.enums.DeliveryState;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDto {
    String deliveryId;
    @NotBlank(message = "ID заказа не может быть пустым")
    String orderId;
    @Builder.Default
    DeliveryState deliveryState = DeliveryState.CREATED;
    @NotNull(message = "Адрес отправки не может быть пустым")
    AddressDto fromAddress;
    @NotNull(message = "Адрес доставки не может быть пустым")
    AddressDto toAddress;
}
