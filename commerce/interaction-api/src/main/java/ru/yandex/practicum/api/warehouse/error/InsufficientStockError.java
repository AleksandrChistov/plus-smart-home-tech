package ru.yandex.practicum.api.warehouse.error;

import java.util.List;

public class InsufficientStockError extends RuntimeException {
    public final List<InsufficientItemDto> insufficientItems;

    public InsufficientStockError(List<InsufficientItemDto> insufficientItems) {
        super("В данным момент на складе нет требуемого количества товара");
        this.insufficientItems = insufficientItems;
    }
}
