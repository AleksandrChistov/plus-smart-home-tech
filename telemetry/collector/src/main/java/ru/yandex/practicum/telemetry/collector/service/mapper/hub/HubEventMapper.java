package ru.yandex.practicum.telemetry.collector.service.mapper.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventMapper {

    HubEventAvro map(HubEventProto event);

    HubEventProto.PayloadCase getMessageType();

}
