package service.mapper.hub;

import model.hub.HubEvent;
import model.hub.enums.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventMapper {

    HubEventAvro map(HubEvent event);

    HubEventType getMessageType();

}
