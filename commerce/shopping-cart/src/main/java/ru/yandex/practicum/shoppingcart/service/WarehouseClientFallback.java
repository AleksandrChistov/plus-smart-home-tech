package ru.yandex.practicum.shoppingcart.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;
import ru.yandex.practicum.api.warehouse.error.ServiceUnavailableException;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {
    @Override
    public void addProduct(NewProductInWarehouseRequest newProductRequest) throws ProductAlreadyExistError {
        log.error("Товар {} не был добавлен на склад", newProductRequest);
        throw new ServiceUnavailableException("WarehouseClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCartDto) throws InsufficientStockError {
        log.error("Ошибка проверки кол-ва товаров на складе для корзины продуктов: {}. Используем фолбэк.", shoppingCartDto);

        // Возвращаем заглушку с параметрами по умолчанию
        return BookedProductsDto.builder()
                .deliveryWeight(0.0f)
                .deliveryVolume(0.0f)
                .fragile(false)
                .build();
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest addProductRequest) throws NotFoundException {
        log.error("Товар {} в кол-ве {} НЕ был добавлен на склад", addProductRequest.getProductId(), addProductRequest.getQuantity());
        throw new ServiceUnavailableException("WarehouseClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.error("Ошибка при получении адреса склада. Используем фолбэк.");

        // Возвращаем заглушку с параметрами по умолчанию
        final String MAIN_ADDRESS = "ADDRESS_1";

        return AddressDto.builder()
                .country(MAIN_ADDRESS)
                .city(MAIN_ADDRESS)
                .street(MAIN_ADDRESS)
                .house(MAIN_ADDRESS)
                .flat(MAIN_ADDRESS)
                .build();
    }
}
