package ru.yandex.practicum.order.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import ru.yandex.practicum.api.shared.error.ApiError;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shared.error.ServiceUnavailableException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    private final ObjectMapper objectMapper;

    public ClientErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("Order methodKey: {}, response: {}", methodKey, response);

        try {
            ApiError error = parseErrorBody(response);

            return switch (response.status()) {
                case 400 -> new BadRequestException(error.getMessage());
                case 404 -> new NotFoundException(error.getMessage());
                case 503 -> new ServiceUnavailableException(error.getMessage());
                default -> defaultDecoder.decode(methodKey, response);
            };
        } catch (Exception e) {
            log.warn("Сломался в ClientErrorDecoder, methodKey {}", methodKey, e);
        }

        return defaultDecoder.decode(methodKey, response);
    }

    private ApiError parseErrorBody(Response response) {
        try {
            if (response.body() != null) {
                String body = StreamUtils.copyToString(
                        response.body().asInputStream(),
                        StandardCharsets.UTF_8
                );

                if (!body.trim().isEmpty()) {
                    return objectMapper.readValue(body, ApiError.class);
                }
            }
        } catch (IOException e) {
            log.warn("Сломался в ClientErrorDecoder на парсинге объекта response {}", response, e);
        }
        return null;
    }
}
