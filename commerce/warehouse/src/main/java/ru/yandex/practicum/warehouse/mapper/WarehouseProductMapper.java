package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.dal.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.dal.model.WarehouseStock;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dimension.width", target = "width")
    @Mapping(source = "dimension.height", target = "height")
    @Mapping(source = "dimension.depth", target = "depth")
    @Mapping(target = "stock", ignore = true)
    WarehouseProduct toModel(NewProductInWarehouseRequest newProductRequest);

    @AfterMapping
    default void initStock(@MappingTarget WarehouseProduct product) {
        WarehouseStock stock = new WarehouseStock();
        stock.setProductId(product.getProductId());
        stock.setQuantity(0);
        product.setStock(stock);
    }

}
