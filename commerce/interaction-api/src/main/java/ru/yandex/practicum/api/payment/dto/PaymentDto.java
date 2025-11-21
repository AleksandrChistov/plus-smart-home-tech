package ru.yandex.practicum.api.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    @NotBlank(message = "ID платежа не может быть пустым")
    String paymentId;
    @NotNull(message = "Общая стоимость должна быть указана")
    @DecimalMin(value = "0.00", message = "Общая стоимость не может быть меньше 0 руб.")
    @Digits(integer = 19, fraction = 2, message = "Общая стоимость поддерживает максимум 19 цифр и 2 после запятой")
    BigDecimal totalPayment;
    @NotNull(message = "Стоимость доставки должна быть указана")
    @DecimalMin(value = "0.00", message = "Стоимость доставки не может быть меньше 0 руб.")
    @Digits(integer = 19, fraction = 2, message = "Стоимость доставки поддерживает максимум 19 цифр и 2 после запятой")
    BigDecimal deliveryTotal;
    @NotNull(message = "Стоимость налога должна быть указана")
    @DecimalMin(value = "0.00", message = "Стоимость налога не может быть меньше 0 руб.")
    @Digits(integer = 19, fraction = 2, message = "Стоимость налога поддерживает максимум 19 цифр и 2 после запятой")
    BigDecimal feeTotal;
}
