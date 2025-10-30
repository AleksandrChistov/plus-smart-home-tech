package ru.yandex.practicum.telemetry.analyzer.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.SnapshotService;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class SnapshotServiceImpl implements SnapshotService {

    private final HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotServiceImpl(@GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    @Override
    public boolean isConditionMatch(String sensorId, Map<String, Condition> conditions, Map<String, SensorStateAvro> sensorStates) {
        Condition condition = conditions.get(sensorId);
        SensorStateAvro sensorState = sensorStates.get(sensorId);

        return switch (condition.getType()) {
            case ConditionType.TEMPERATURE -> {
                int temperature;
                if (sensorState.getData() instanceof TemperatureSensorAvro temperatureData) {
                    temperature = temperatureData.getTemperatureC();
                } else if (sensorState.getData() instanceof ClimateSensorAvro climateData) {
                    temperature = climateData.getTemperatureC();
                } else {
                    log.warn("Тип данных устройства {} не соответствует типу условия {}", sensorId, ConditionType.TEMPERATURE);
                    yield false;
                }
                yield isValueMatch(temperature, condition.getOperation(), condition.getValue());
            }
            case ConditionType.CO2LEVEL -> sensorState.getData() instanceof ClimateSensorAvro climateData &&
                    isValueMatch(climateData.getCo2Level(), condition.getOperation(), condition.getValue());
            case ConditionType.HUMIDITY -> sensorState.getData() instanceof ClimateSensorAvro climateData &&
                    isValueMatch(climateData.getHumidity(), condition.getOperation(), condition.getValue());
            case ConditionType.MOTION -> sensorState.getData() instanceof MotionSensorAvro motionData &&
                    isValueMatch(motionData.getMotion() ? 1 : 0, condition.getOperation(), condition.getValue());
            case ConditionType.LUMINOSITY -> sensorState.getData() instanceof LightSensorAvro lightData &&
                    isValueMatch(lightData.getLuminosity(), condition.getOperation(), condition.getValue());
            case ConditionType.SWITCH -> sensorState.getData() instanceof SwitchSensorAvro switchData &&
                    isValueMatch(switchData.getState() ? 1 : 0, condition.getOperation(), condition.getValue());
        };
    }

    private boolean isValueMatch(int sensorValue, ConditionOperation operation, int targetValue) {
        return switch (operation) {
            case ConditionOperation.EQUALS -> sensorValue == targetValue;
            case ConditionOperation.GREATER_THAN -> sensorValue > targetValue;
            case ConditionOperation.LOWER_THAN -> sensorValue < targetValue;
        };
    }

    @Override
    public DeviceActionRequest buildActionRequest(Scenario scenario, Map.Entry<String, Action> entry) {
        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(entry.getKey())
                        .setType(ActionTypeProto.valueOf(entry.getValue().getType().name()))
                        .setValue(entry.getValue().getValue())
                        .build())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano()))
                .build();
    }

    @Override
    public void sendRequest(DeviceActionRequest request, String hubId) {
        try {
            Empty response = hubRouterClient.handleDeviceAction(request);
            if (response != null) {
                log.info("Действие {} успешно отправлено на hubId {}", request.getAction(), hubId);
            } else {
                log.warn("При отправке действия {} на hubId {} пришел ответ null", request.getAction(), hubId);
            }
        } catch (StatusRuntimeException e) {
            log.error("Отправка действия {} на hubId {} завершилась ошибкой {}", request.getAction(), hubId, e.getMessage());
            throw new RuntimeException("Отправка действия " + request.getAction() + "на hubId " + hubId + " завершилась ошибкой", e);
        }
    }

}
