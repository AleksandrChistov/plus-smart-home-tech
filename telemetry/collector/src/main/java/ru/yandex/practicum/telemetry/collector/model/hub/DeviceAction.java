package ru.yandex.practicum.telemetry.collector.model.hub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.ActionType;

/**
 * Представляет действие, которое должно быть выполнено устройством.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
