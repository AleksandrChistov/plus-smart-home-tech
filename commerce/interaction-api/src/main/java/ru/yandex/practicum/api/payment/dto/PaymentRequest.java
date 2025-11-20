package ru.yandex.practicum.api.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "ID платежа должно быть указано")
    String paymentId;
}
