package ru.yandex.practicum.telemetry.collector.service.mapper.hub;

import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventMapper {

    HubEventAvro map(HubEvent event);

    HubEventType getMessageType();

}
