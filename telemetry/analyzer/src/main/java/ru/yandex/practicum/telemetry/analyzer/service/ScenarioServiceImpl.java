package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.dao.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ActionType;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.ScenarioService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioServiceImpl implements ScenarioService {

    private final ScenarioRepository scenarioRepository;

    @Override
    public Scenario add(ScenarioAddedEventAvro event, String hubId) {
        return scenarioRepository.findByHubIdAndName(hubId, event.getName()).orElseGet(() -> {

            Map<String, Condition> conditions = event.getConditions().stream()
                    .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, condition -> {
                        Integer value = switch (condition.getValue()) {
                            case Integer i -> i;
                            case Boolean b -> b ? 1 : 0;
                            default -> null;
                        };

                        return Condition.builder()
                                .type(ConditionType.valueOf(condition.getType().name()))
                                .operation(ConditionOperation.valueOf(condition.getOperation().name()))
                                .value(value)
                                .build();
                    }));

            Map<String, Action> actions = event.getActions().stream()
                    .collect(Collectors.toMap(DeviceActionAvro::getSensorId, action -> Action.builder()
                            .type(ActionType.valueOf(action.getType().name()))
                            .value(action.getValue())
                            .build()));

            Scenario scenario = Scenario.builder()
                    .hubId(hubId)
                    .name(event.getName())
                    .conditions(conditions)
                    .actions(actions)
                    .build();

            return scenarioRepository.save(scenario);
        });
    }

    @Override
    public void delete(ScenarioRemovedEventAvro event, String hubId) {
        scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .ifPresent(scenarioRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Scenario> findByHubId(String hubId) {
        return scenarioRepository.findByHubId(hubId);
    }
}
