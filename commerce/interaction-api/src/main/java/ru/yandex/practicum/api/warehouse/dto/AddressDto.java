package ru.yandex.practicum.api.warehouse.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
