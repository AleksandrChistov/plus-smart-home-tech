package ru.yandex.practicum.order.client.delivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.api.delivery.dto.DeliveryRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shared.error.ServiceUnavailableException;

import java.math.BigDecimal;

@Slf4j
@Component
public class DeliveryClientFallback implements DeliveryClient {
    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        log.error("Доставка не была создана {}", deliveryDto);
        throw new ServiceUnavailableException("DeliveryClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public BigDecimal calculateTotal(OrderDto orderDto) throws NotFoundException {
        log.error("Стоимость доставки не была рассчитана {}", orderDto);
        throw new ServiceUnavailableException("DeliveryClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public void picked(DeliveryRequest request) throws NotFoundException {
        log.error("Товар не был получен в доставку {}", request);
        throw new ServiceUnavailableException("DeliveryClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public void successful(DeliveryRequest request) throws NotFoundException {
        log.error("Доставка не была выполнена {}", request);
        throw new ServiceUnavailableException("DeliveryClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public void failed(DeliveryRequest request) throws NotFoundException {
        log.error("Доставка не была отменена {}", request);
        throw new ServiceUnavailableException("DeliveryClient временно недоступен. Попробуйте позже.");
    }
}
