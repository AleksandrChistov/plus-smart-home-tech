package ru.yandex.practicum.api.warehouse.error;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsufficientItemDto {
    private String productId;
    private Integer requestedQuantity;
    private Integer availableQuantity;
}
