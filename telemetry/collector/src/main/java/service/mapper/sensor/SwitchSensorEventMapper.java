package service.mapper.sensor;

import model.sensor.SensorEvent;
import model.sensor.SwitchSensorEvent;
import model.sensor.enums.SensorEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEvent sensorEvent) {
        SwitchSensorEvent event = (SwitchSensorEvent) sensorEvent;

        SwitchSensorAvro switchSensorEventAvro = SwitchSensorAvro.newBuilder()
                .setState(event.isState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(switchSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}
