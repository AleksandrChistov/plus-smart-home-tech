package ru.yandex.practicum.telemetry.analyzer.service.interfaces;

import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;

import java.util.List;

public interface ScenarioService {

    Scenario add(ScenarioAddedEventAvro event, String hubId);

    void delete(ScenarioRemovedEventAvro event, String hubId);

    List<Scenario> findByHubId(String hubId);

}
