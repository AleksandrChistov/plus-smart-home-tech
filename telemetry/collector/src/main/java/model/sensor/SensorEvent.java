package model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.sensor.enums.SensorEventType;

import java.time.Instant;

@Getter
@Setter
@ToString
public abstract class SensorEvent {
    private String id;
    private String hubId;
    private Instant timestamp = Instant.now();

    public abstract SensorEventType getType();
}
