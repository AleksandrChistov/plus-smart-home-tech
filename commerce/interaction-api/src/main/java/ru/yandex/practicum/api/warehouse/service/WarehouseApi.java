package ru.yandex.practicum.api.warehouse.service;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;

public interface WarehouseApi {
    String URL = "/api/v1/warehouse";

    @PutMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductRequest) throws ProductAlreadyExistError;

    /**
     * Предварительно проверить что количество товаров на складе достаточно для данной корзины продуктов.
     */
    @PostMapping(path = URL + "/check", consumes = MediaType.APPLICATION_JSON_VALUE)
    BookedProductsDto checkProducts(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws InsufficientStockError;

    @PostMapping(path = URL + "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    void addQuantity(@RequestBody @Valid AddProductToWarehouseRequest addProductRequest) throws NotFoundException;

    /**
     * Предоставить адрес склада для расчёта доставки.
     */
    @GetMapping(path = URL + "/address", produces = MediaType.APPLICATION_JSON_VALUE)
    AddressDto getWarehouseAddress();

}