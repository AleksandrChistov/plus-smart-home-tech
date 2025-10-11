package model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class ScenarioCondition {
    @NotBlank
    private String sensorId;
    @NotNull
    private ConditionType type;
    @NotNull
    private ConditionOperation operation;
    private Object value; // int, boolean или null
}
