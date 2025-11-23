package ru.yandex.practicum.api.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyProductsForOrderRequest {
    @NotNull(message = "ID заказа должен быть указан")
    String orderId;
    @NotNull(message = "Товары должны быть заполнены")
    Map<String, Integer> products;
}
