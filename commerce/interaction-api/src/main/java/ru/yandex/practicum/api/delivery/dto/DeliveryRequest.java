package ru.yandex.practicum.api.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequest {
    @NotBlank(message = "ID доставки должно быть указано")
    String deliveryId;
}
