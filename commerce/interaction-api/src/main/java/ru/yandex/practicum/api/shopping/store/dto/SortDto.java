package ru.yandex.practicum.api.shopping.store.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SortDto {
    String direction;
    String property;
}
