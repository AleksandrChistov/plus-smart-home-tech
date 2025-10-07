package model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.hub.enums.ConditionOperation;
import model.hub.enums.ConditionType;

/**
 * Условие сценария, которое содержит информацию о датчике, типе условия, операции и значении.
 */
@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Object value; // int, boolean или null
}
