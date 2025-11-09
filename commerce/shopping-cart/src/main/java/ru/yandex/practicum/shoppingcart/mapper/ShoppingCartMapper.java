package ru.yandex.practicum.shoppingcart.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.dal.model.ShoppingCart;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShoppingCartMapper {

    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shoppingCartId", ignore = true)
    @Mapping(target = "cartState", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(source = "products", target = "products")
    void updateModel(Map<String, Integer> products, @MappingTarget ShoppingCart product);

}
