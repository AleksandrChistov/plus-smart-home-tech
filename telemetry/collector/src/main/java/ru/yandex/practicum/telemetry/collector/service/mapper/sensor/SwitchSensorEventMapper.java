package ru.yandex.practicum.telemetry.collector.service.mapper.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

import java.time.Instant;

@Component
public class SwitchSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEventProto event) {
        SwitchSensorProto switchSensor = event.getSwitch();

        SwitchSensorAvro switchSensorEventAvro = SwitchSensorAvro.newBuilder()
                .setState(switchSensor.getState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(switchSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH;
    }

}
