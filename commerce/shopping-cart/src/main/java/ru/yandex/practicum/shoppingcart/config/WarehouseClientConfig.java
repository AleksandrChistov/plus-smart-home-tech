package ru.yandex.practicum.shoppingcart.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.shoppingcart.error.WarehouseClientErrorDecoder;

@Configuration
@RequiredArgsConstructor
public class WarehouseClientConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .errorDecoder(new WarehouseClientErrorDecoder(objectMapper));
    }

}


