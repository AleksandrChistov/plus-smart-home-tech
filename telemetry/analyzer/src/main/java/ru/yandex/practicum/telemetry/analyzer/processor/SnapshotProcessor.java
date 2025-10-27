package ru.yandex.practicum.telemetry.analyzer.processor;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.kafka.KafkaFactory;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.ScenarioService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SnapshotProcessor {

    private final KafkaFactory kafkaFactory;

    private final ScenarioService scenarioService;

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotProcessor(KafkaFactory kafkaFactory, ScenarioService scenarioService,
                             @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient
    ) {
        this.kafkaFactory = kafkaFactory;
        this.scenarioService = scenarioService;
        this.hubRouterClient = hubRouterClient;
    }

    public void process() {
        final Consumer<String, SensorsSnapshotAvro> consumer = kafkaFactory.createConsumer(SnapshotProcessor.class.getSimpleName());

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал на завершение работы в {}", SnapshotProcessor.class.getSimpleName());
                consumer.wakeup();
            }));

            consumer.subscribe(kafkaFactory.getTopics(SnapshotProcessor.class.getSimpleName()));

            while (true) {
                Long pollTimeout = kafkaFactory.getPollTimeout(SnapshotProcessor.class.getSimpleName());

                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();

                    log.info("Получен снапшот: {}", snapshot.toString());

                    List<Scenario> scenarios = scenarioService.findByHubId(snapshot.getHubId());

                    Map<String, SensorStateAvro> sensorStates = snapshot.getSensorState();

                    List<DeviceActionRequest> deviceActionRequests = scenarios.stream()
                            .filter(scenario -> sensorStates.keySet().containsAll(scenario.getConditions().keySet()))
                            .filter(scenario -> scenario.getConditions().keySet().stream()
                                    .allMatch(sensorId -> isConditionMatch(sensorId, scenario.getConditions(), sensorStates)))
                            .flatMap(scenario -> scenario.getActions().entrySet().stream()
                                    .map(entry -> buildActionRequest(scenario, entry)))
                            .toList();


                    deviceActionRequests.forEach(actionRequest -> sendRequest(actionRequest, snapshot.getHubId()));

                    // at-least-once семантика
                    log.debug("Асинхронно фиксируем смещения обработанных сообщений");
                    consumer.commitAsync((offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Во время фиксации произошла ошибка. Cмещения: {}", offsets, exception);
                        }
                    });
                }
            }
        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий датчиков", e);
        } finally {
            try {
                log.debug("Очистка буфера и фиксация смещений");
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private boolean isConditionMatch(String sensorId, Map<String, Condition> conditions, Map<String, SensorStateAvro> sensorStates) {
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

    private DeviceActionRequest buildActionRequest(Scenario scenario, Map.Entry<String, Action> entry) {
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

    private void sendRequest(DeviceActionRequest request, String hubId) {
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
