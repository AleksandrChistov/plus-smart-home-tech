package ru.yandex.practicum.payment.client.order;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
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
public class OrderClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    private final ObjectMapper objectMapper;

    public OrderClientErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("Order methodKey: {}, response: {}", methodKey, response);

        try {
            if (((methodKey.contains("payOrder") || methodKey.contains("payOrderFailed")) && response.status() == 400)
                    || response.status() == 404
            ) {
                ApiError error = parseErrorBody(response);
                return new NotFoundException(error.getMessage());
            }

            if (response.status() == 503) {
                ApiError error = parseErrorBody(response);
                return new ServiceUnavailableException(error.getMessage());
            }

        } catch (Exception e) {
            log.warn("Сломался в OrderClientErrorDecoder, methodKey {}", methodKey, e);
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
            log.warn("Сломался в OrderClientErrorDecoder на парсинге объекта response {}", response, e);
        }
        return null;
    }
}
