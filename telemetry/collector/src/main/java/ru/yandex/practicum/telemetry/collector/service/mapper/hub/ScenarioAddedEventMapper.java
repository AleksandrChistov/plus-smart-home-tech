package ru.yandex.practicum.telemetry.collector.service.mapper.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
public class ScenarioAddedEventMapper implements HubEventMapper {

    public HubEventAvro map(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();

        List<ScenarioConditionAvro> conditionsAvro = scenarioAddedEvent.getConditionsList().stream()
                .map(cond -> {
                    Object value = switch (cond.getValueCase()) {
                        case BOOL_VALUE -> cond.getBoolValue();
                        case INT_VALUE -> cond.getIntValue();
                        default -> null;
                    };

                    return ScenarioConditionAvro.newBuilder()
                            .setSensorId(cond.getSensorId())
                            .setType(ConditionTypeAvro.valueOf(cond.getType().name()))
                            .setOperation(ConditionOperationAvro.valueOf(cond.getOperation().name()))
                            .setValue(value)
                            .build();
                        }
                )
                .toList();

        List<DeviceActionAvro> actionsAvro = scenarioAddedEvent.getActionsList().stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build()
                )
                .toList();

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(conditionsAvro)
                .setActions(actionsAvro)
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(scenarioAddedEventAvro)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

}
