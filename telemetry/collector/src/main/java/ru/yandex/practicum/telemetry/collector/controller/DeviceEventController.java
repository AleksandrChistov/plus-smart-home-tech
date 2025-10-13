package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaDeviceEventProducer;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.mapper.hub.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.service.mapper.sensor.SensorEventMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@Slf4j
public class DeviceEventController {

    private final String HUB_TOPIC;
    private final String SENSOR_TOPIC;

    private final KafkaDeviceEventProducer kafkaProducer;
    private final Map<HubEventType, HubEventMapper> hubEventMappers;
    private final Map<SensorEventType, SensorEventMapper> sensorEventMappers;

    public DeviceEventController(
            @Value("${telemetry.collector.kafka.hub.topic}") String hubTopic, @Value("${telemetry.collector.kafka.sensor.topic}") String sensorTopic,
            KafkaDeviceEventProducer kafkaProducer, List<HubEventMapper> hubEventMappers, List<SensorEventMapper> sensorEventMappers
    ) {
        HUB_TOPIC = hubTopic;
        SENSOR_TOPIC = sensorTopic;

        this.kafkaProducer = kafkaProducer;

        this.hubEventMappers = hubEventMappers.stream()
                .collect(Collectors.toMap(HubEventMapper::getMessageType, Function.identity()));

        this.sensorEventMappers = sensorEventMappers.stream()
                .collect(Collectors.toMap(SensorEventMapper::getMessageType, Function.identity()));
    }

    @PostMapping("/hubs")
    public void sendHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("HubEvent JSON: {}", event.toString());

        HubEventMapper hubEventMapper = Optional.of(hubEventMappers.get(event.getType()))
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип события для хаба"));

        HubEventAvro hubEventAvro = hubEventMapper.map(event);

        kafkaProducer.send(HUB_TOPIC, hubEventAvro.getTimestamp().toEpochMilli(), hubEventAvro.getHubId(), hubEventAvro);
    }

    @PostMapping("/sensors")
    public void sendSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("SensorEvent JSON: {}", event.toString());

        SensorEventMapper sensorEventMapper = Optional.of(sensorEventMappers.get(event.getType()))
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип события для датчика"));

        SensorEventAvro sensorEventAvro = sensorEventMapper.map(event);

        kafkaProducer.send(SENSOR_TOPIC, sensorEventAvro.getTimestamp().toEpochMilli(), sensorEventAvro.getHubId(), sensorEventAvro);
    }

}
