package ru.yandex.practicum.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.order.client.ClientErrorDecoder;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public Feign.Builder feignOrderBuilder() {
        return Feign.builder()
                .errorDecoder(new ClientErrorDecoder(objectMapper));
    }

    @Bean
    public Feign.Builder feignWarehouseBuilder() {
        return Feign.builder()
                .errorDecoder(new ClientErrorDecoder(objectMapper));
    }

    @Bean
    public Feign.Builder feignPaymentBuilder() {
        return Feign.builder()
                .errorDecoder(new ClientErrorDecoder(objectMapper));
    }

}


