package service.mapper.sensor;

import model.sensor.MotionSensorEvent;
import model.sensor.SensorEvent;
import model.sensor.enums.SensorEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class MotionSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEvent sensorEvent) {
        MotionSensorEvent event = (MotionSensorEvent) sensorEvent;

        MotionSensorAvro motionSensorEventAvro = MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(motionSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

}
