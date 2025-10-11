package service.mapper.sensor;

import model.sensor.ClimateSensorEvent;
import model.sensor.SensorEvent;
import model.sensor.enums.SensorEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class ClimateSensorEventMapper implements SensorEventMapper {

    public SensorEventAvro map(SensorEvent sensorEvent) {
        ClimateSensorEvent event = (ClimateSensorEvent) sensorEvent;

        ClimateSensorAvro climateSensorEventAvro = ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(climateSensorEventAvro)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

}
