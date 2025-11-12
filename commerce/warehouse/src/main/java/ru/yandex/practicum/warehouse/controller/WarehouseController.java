package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;
import ru.yandex.practicum.api.warehouse.service.WarehouseApi;
import ru.yandex.practicum.warehouse.service.WarehouseProductService;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {

    private final WarehouseProductService warehouseProductService;

    @Override
    public void addProduct(NewProductInWarehouseRequest newProductRequest) throws ProductAlreadyExistError {
        warehouseProductService.addProduct(newProductRequest);
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCartDto) throws InsufficientStockError {
        return warehouseProductService.checkProducts(shoppingCartDto);
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest addProductRequest) throws NotFoundException {
        warehouseProductService.addQuantity(addProductRequest);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseProductService.getWarehouseAddress();
    }
}
