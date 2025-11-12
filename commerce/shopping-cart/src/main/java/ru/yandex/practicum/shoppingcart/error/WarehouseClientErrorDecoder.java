package ru.yandex.practicum.shoppingcart.error;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.warehouse.error.InsufficientItemDto;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class WarehouseClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    private final ObjectMapper objectMapper;

    public WarehouseClientErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("Method key: {}, response: {}", methodKey, response);

        try {
            if (methodKey.contains("checkProducts") && response.status() == 400) {
                ApiError error = parseErrorBody(response);
                return new InsufficientStockError((List<InsufficientItemDto>) error.getInfo());
            }

            if (methodKey.contains("addProduct") && response.status() == 400) {
                ApiError error = parseErrorBody(response);
                return new ProductAlreadyExistError(error.getMessage());
            }

            if (response.status() == 404) {
                ApiError error = parseErrorBody(response);
                return new NotFoundException(error.getMessage());
            }

        } catch (Exception e) {
            log.warn("Сломался на парсинге ошибки от {} в WarehouseClientErrorDecoder", methodKey, e);
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
            log.debug("Сломался в WarehouseClientErrorDecoder на парсинге объекта response", e);
        }
        return null;
    }
}
