package model.hub;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.hub.enums.HubEventType;

import java.util.Set;

/**
 * Событие добавления сценария в систему. Содержит информацию о названии сценария, условиях и действиях.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @Size(min = 3)
    private String name;
    private Set<ScenarioCondition> conditions;
    private Set<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
