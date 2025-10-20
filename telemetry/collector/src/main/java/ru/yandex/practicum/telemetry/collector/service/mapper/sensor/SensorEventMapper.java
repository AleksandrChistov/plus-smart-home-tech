package ru.yandex.practicum.telemetry.collector.service.mapper.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventMapper {

    SensorEventAvro map(SensorEventProto event);

    SensorEventProto.PayloadCase getMessageType();

}
