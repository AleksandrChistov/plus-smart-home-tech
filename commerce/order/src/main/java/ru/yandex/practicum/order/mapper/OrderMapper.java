package ru.yandex.practicum.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.order.dal.model.Order;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDto toDto(Order order);

    @Mapping(source = "orderDto.products", target = "products")
    @Mapping(source = "username", target = "username")
    Order toModel(OrderDto orderDto, String username);

    List<OrderDto> toDtoList(List<Order> orders);

}
