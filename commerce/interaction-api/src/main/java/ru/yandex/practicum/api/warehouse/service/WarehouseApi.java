package ru.yandex.practicum.api.warehouse.service;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.*;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;

import java.util.Map;

public interface WarehouseApi {
    String URL = "/api/v1/warehouse";

    @PutMapping(path = URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductRequest) throws ProductAlreadyExistError;

    /**
     * Предварительно проверить что количество товаров на складе достаточно для данной корзины продуктов.
     */
    @PostMapping(path = URL + "/check", consumes = MediaType.APPLICATION_JSON_VALUE)
    BookedProductsDto checkProducts(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws InsufficientStockError;

    /**
     * Собрать товары к заказу для подготовки к отправке.
     */
    @PostMapping(path = URL + "/assembly", consumes = MediaType.APPLICATION_JSON_VALUE)
    BookedProductsDto assemblyProducts(@RequestBody @Valid AssemblyProductsForOrderRequest request) throws InsufficientStockError;

    /**
     * Передать товары в доставку.
     */
    @PostMapping(path = URL + "/shipped", consumes = MediaType.APPLICATION_JSON_VALUE)
    void shipProducts(@RequestBody @Valid ShippedToDeliveryRequest request) throws NotFoundException;

    @PostMapping(path = URL + "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    void addQuantity(@RequestBody @Valid AddProductToWarehouseRequest addProductRequest) throws NotFoundException;

    /**
     * Принять возврат товаров на склад.
     */
    @PostMapping(path = URL + "/return", consumes = MediaType.APPLICATION_JSON_VALUE)
    void returnProducts(@RequestBody @Valid Map<String, Integer> products);

    /**
     * Предоставить адрес склада для расчёта доставки.
     */
    @GetMapping(path = URL + "/address", produces = MediaType.APPLICATION_JSON_VALUE)
    AddressDto getWarehouseAddress();

}