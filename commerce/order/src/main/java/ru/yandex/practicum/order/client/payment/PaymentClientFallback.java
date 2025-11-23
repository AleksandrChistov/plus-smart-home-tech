package ru.yandex.practicum.order.client.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.payment.dto.PaymentRequest;
import ru.yandex.practicum.api.shared.error.ServiceUnavailableException;

import java.math.BigDecimal;

@Slf4j
@Component
public class PaymentClientFallback implements PaymentClient {
    @Override
    public PaymentDto payment(OrderDto orderDto) {
        log.error("Процесс оплаты не был запущен {}", orderDto);
        throw new ServiceUnavailableException("PaymentClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        log.error("Стоимость товаров не была рассчитана {}", orderDto);
        throw new ServiceUnavailableException("PaymentClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        log.error("Стоимость закзаа не была рассчитана {}", orderDto);
        throw new ServiceUnavailableException("PaymentClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public void refund(PaymentRequest request) {
        log.error("Оплата не была произведена {}", request);
        throw new ServiceUnavailableException("PaymentClient временно недоступен. Попробуйте позже.");
    }

    @Override
    public void failed(PaymentRequest request) {
        log.error("Отказ в оплате не был произведен {}", request);
        throw new ServiceUnavailableException("PaymentClient временно недоступен. Попробуйте позже.");
    }
}
