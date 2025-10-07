package model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.hub.enums.HubEventType;

import java.time.Instant;

@Getter
@Setter
@ToString
public abstract class HubEvent {
    private String hubId;
    private Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}
