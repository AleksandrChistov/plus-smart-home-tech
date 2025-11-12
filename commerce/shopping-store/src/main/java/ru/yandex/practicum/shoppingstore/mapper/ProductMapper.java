package ru.yandex.practicum.shoppingstore.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.api.shopping.store.dto.ProductContentDto;
import ru.yandex.practicum.shoppingstore.dal.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductContentDto toDto(Product product);

    @Mapping(target = "productId", ignore = true)
    Product toModel(ProductContentDto productDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productId", ignore = true)
    void updateModel(ProductContentDto productDto, @MappingTarget Product product);

}
