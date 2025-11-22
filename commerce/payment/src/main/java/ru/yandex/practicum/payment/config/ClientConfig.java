package ru.yandex.practicum.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.payment.client.order.OrderClientErrorDecoder;
import ru.yandex.practicum.payment.client.shoppingstore.ShoppingStoreClientErrorDecoder;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public Feign.Builder feignShoppingStoreBuilder() {
        return Feign.builder()
                .errorDecoder(new ShoppingStoreClientErrorDecoder(objectMapper));
    }

    @Bean
    public Feign.Builder feignOrderBuilder() {
        return Feign.builder()
                .errorDecoder(new OrderClientErrorDecoder(objectMapper));
    }

}


