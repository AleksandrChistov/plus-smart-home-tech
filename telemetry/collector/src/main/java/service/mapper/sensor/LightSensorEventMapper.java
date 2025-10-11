package service.mapper.sensor;

import model.sensor.LightSensorEvent;
import model.sensor.SensorEvent;
import model.sensor.enums.SensorEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class LightSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEvent sensorEvent) {
        LightSensorEvent event = (LightSensorEvent) sensorEvent;

        LightSensorAvro lightSensorEventAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(lightSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

}
