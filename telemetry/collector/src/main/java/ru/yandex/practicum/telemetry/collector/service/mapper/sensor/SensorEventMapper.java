package ru.yandex.practicum.telemetry.collector.service.mapper.sensor;

import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventMapper {

    SensorEventAvro map(SensorEvent event);

    SensorEventType getMessageType();

}
