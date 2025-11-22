package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.dal.model.Delivery;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeliveryMapper {

    @Mapping(target = "fromCountry", source = "fromAddress.country")
    @Mapping(target = "fromCity", source = "fromAddress.city")
    @Mapping(target = "fromStreet", source = "fromAddress.street")
    @Mapping(target = "fromHouse", source = "fromAddress.house")
    @Mapping(target = "fromFlat", source = "fromAddress.flat")
    @Mapping(target = "toCountry", source = "toAddress.country")
    @Mapping(target = "toCity", source = "toAddress.city")
    @Mapping(target = "toStreet", source = "toAddress.street")
    @Mapping(target = "toHouse", source = "toAddress.house")
    @Mapping(target = "toFlat", source = "toAddress.flat")
    Delivery toModel(DeliveryDto deliveryDto);


    @Mapping(target = "fromAddress.country", source = "fromCountry")
    @Mapping(target = "fromAddress.city", source = "fromCity")
    @Mapping(target = "fromAddress.street", source = "fromStreet")
    @Mapping(target = "fromAddress.house", source = "fromHouse")
    @Mapping(target = "fromAddress.flat", source = "fromFlat")
    @Mapping(target = "toAddress.country", source = "toCountry")
    @Mapping(target = "toAddress.city", source = "toCity")
    @Mapping(target = "toAddress.street", source = "toStreet")
    @Mapping(target = "toAddress.house", source = "toHouse")
    @Mapping(target = "toAddress.flat", source = "toFlat")
    DeliveryDto toDto(Delivery delivery);

}