package ru.yandex.practicum.shoppingstore.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.api.shopping.store.dto.ProductDto;
import ru.yandex.practicum.shoppingstore.dal.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductDto toDto(Product product);

    @Mapping(target = "productId", ignore = true)
    Product toModel(ProductDto productDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productId", ignore = true)
    void updateModel(ProductDto productDto, @MappingTarget Product product);

}
