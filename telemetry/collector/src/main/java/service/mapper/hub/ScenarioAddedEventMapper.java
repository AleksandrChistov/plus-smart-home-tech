package service.mapper.hub;

import model.hub.HubEvent;
import model.hub.ScenarioAddedEvent;
import model.hub.enums.HubEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
public class ScenarioAddedEventMapper implements HubEventMapper {

    public HubEventAvro map(HubEvent hubEvent) {
        ScenarioAddedEvent event = (ScenarioAddedEvent) hubEvent;

        List<ScenarioConditionAvro> conditionsAvro = event.getConditions().stream()
                .map(cond -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(cond.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(cond.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(cond.getOperation().name()))
                        .setValue(cond.getValue())
                        .build()
                )
                .toList();

        List<DeviceActionAvro> actionsAvro = event.getActions().stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build()
                )
                .toList();

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditionsAvro)
                .setActions(actionsAvro)
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(scenarioAddedEventAvro)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

}
