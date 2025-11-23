package ru.yandex.practicum.order.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.payment.service.PaymentApi;
import ru.yandex.practicum.order.config.ClientConfig;

@FeignClient(name = "payment", configuration = ClientConfig.class, fallback = PaymentClientFallback.class)
public interface PaymentClient extends PaymentApi {
}