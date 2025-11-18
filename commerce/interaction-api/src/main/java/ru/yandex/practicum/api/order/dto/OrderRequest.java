package ru.yandex.practicum.api.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotBlank(message = "ID заказа должно быть указано")
    String orderId;
}
