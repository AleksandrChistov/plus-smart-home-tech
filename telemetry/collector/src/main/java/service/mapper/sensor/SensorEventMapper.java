package service.mapper.sensor;

import model.sensor.SensorEvent;
import model.sensor.enums.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventMapper {

    SensorEventAvro map(SensorEvent event);

    SensorEventType getMessageType();

}
