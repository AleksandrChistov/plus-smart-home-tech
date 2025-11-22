package ru.yandex.practicum.delivery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.delivery.client.order.OrderClientErrorDecoder;
import ru.yandex.practicum.delivery.client.warehouse.WarehouseClientErrorDecoder;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public Feign.Builder feignOrderBuilder() {
        return Feign.builder()
                .errorDecoder(new OrderClientErrorDecoder(objectMapper));
    }

    @Bean
    public Feign.Builder feignWarehouseBuilder() {
        return Feign.builder()
                .errorDecoder(new WarehouseClientErrorDecoder(objectMapper));
    }

}


