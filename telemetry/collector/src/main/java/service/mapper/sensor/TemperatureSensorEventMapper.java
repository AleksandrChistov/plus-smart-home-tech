package service.mapper.sensor;

import model.sensor.SensorEvent;
import model.sensor.TemperatureSensorEvent;
import model.sensor.enums.SensorEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEvent sensorEvent) {
        TemperatureSensorEvent event = (TemperatureSensorEvent) sensorEvent;

        TemperatureSensorAvro temperatureSensorEventAvro = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(temperatureSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

}
