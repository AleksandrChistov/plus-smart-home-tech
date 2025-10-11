package ru.yandex.practicum.telemetry.collector.service.mapper.hub;

import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventMapper implements HubEventMapper {

    public HubEventAvro map(HubEvent hubEvent) {
        ScenarioRemovedEvent event = (ScenarioRemovedEvent) hubEvent;

        ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(scenarioRemovedEventAvro)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

}
