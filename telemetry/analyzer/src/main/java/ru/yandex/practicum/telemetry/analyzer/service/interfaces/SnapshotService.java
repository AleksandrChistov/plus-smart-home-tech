package ru.yandex.practicum.telemetry.analyzer.service.interfaces;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;

import java.util.Map;

public interface SnapshotService {

    boolean isConditionMatch(String sensorId, Map<String, Condition> conditions, Map<String, SensorStateAvro> sensorStates);

    DeviceActionRequest buildActionRequest(Scenario scenario, Map.Entry<String, Action> entry);

    void sendRequest(DeviceActionRequest request, String hubId);

}
