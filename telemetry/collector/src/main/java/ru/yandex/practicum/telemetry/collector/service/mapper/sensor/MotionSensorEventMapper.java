package ru.yandex.practicum.telemetry.collector.service.mapper.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Component
public class MotionSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEventProto event) {
        MotionSensorProto motionSensor = event.getMotion();

        MotionSensorAvro motionSensorEventAvro = MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensor.getLinkQuality())
                .setMotion(motionSensor.getMotion())
                .setVoltage(motionSensor.getVoltage())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(motionSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION;
    }

}
