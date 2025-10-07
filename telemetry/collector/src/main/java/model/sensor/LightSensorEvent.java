package model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.sensor.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private int linkQuality;
    private int luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
