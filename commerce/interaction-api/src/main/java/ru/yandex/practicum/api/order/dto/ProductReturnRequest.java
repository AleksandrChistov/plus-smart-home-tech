package ru.yandex.practicum.api.order.dto;

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
public class ProductReturnRequest {
    @NotBlank(message = "ID заказа не может быть пустым")
    String orderId;
    @NotNull(message = "Список товаров не может отсутствовать")
    Map<String, Integer> products = new HashMap<>();
}
