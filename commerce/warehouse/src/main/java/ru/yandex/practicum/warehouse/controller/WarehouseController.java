package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.*;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;
import ru.yandex.practicum.api.warehouse.service.WarehouseApi;
import ru.yandex.practicum.warehouse.service.WarehouseProductService;

import java.util.Map;

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
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) throws InsufficientStockError {
        return warehouseProductService.assemblyProducts(request);
    }

    @Override
    public void shipProducts(ShippedToDeliveryRequest request) {
        warehouseProductService.shipProducts(request);
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest addProductRequest) throws NotFoundException {
        warehouseProductService.addQuantity(addProductRequest);
    }

    @Override
    public void returnProducts(Map<String, Integer> products) {
        warehouseProductService.returnProducts(products);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseProductService.getWarehouseAddress();
    }
}
