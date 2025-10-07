package model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.hub.enums.DeviceType;
import model.hub.enums.HubEventType;

/**
 * Событие, сигнализирующее о добавлении нового устройства в систему.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    private int id;
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
