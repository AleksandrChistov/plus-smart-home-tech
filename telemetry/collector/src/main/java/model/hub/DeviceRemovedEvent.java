package model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.hub.enums.HubEventType;

/**
 * Событие, сигнализирующее о удалении устройства из системы.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    private int id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
