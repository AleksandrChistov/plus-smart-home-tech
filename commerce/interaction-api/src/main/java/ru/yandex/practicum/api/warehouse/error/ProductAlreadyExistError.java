package ru.yandex.practicum.api.warehouse.error;

public class ProductAlreadyExistError extends RuntimeException {
    public ProductAlreadyExistError(String productId) {
        super("Товар уже зарегистрирован, id = " + productId);
    }
}
