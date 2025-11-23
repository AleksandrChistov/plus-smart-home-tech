package ru.yandex.practicum.delivery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.delivery.client.order.OrderClientErrorDecoder;

@Configuration
@RequiredArgsConstructor
public class OrderClientConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public Feign.Builder feignOrderBuilder() {
        return Feign.builder()
                .errorDecoder(new OrderClientErrorDecoder(objectMapper));
    }

}


