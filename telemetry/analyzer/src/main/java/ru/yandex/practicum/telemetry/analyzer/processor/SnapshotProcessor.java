package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.kafka.KafkaFactory;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.ScenarioService;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.SnapshotService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SnapshotProcessor {

    private final KafkaFactory kafkaFactory;

    private final ScenarioService scenarioService;

    private final SnapshotService snapshotService;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final int BATCH_SIZE = 50;

    public void process() {
        final Consumer<String, SensorsSnapshotAvro> consumer = kafkaFactory.createConsumer(SnapshotProcessor.class.getSimpleName());
        final Duration pollTimeout = Duration.ofMillis(kafkaFactory.getPollTimeout(SnapshotProcessor.class.getSimpleName()));

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал на завершение работы в {}", SnapshotProcessor.class.getSimpleName());
                consumer.wakeup();
            }));

            consumer.subscribe(kafkaFactory.getTopics(SnapshotProcessor.class.getSimpleName()));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(pollTimeout);
                int count = 0;

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(consumer, record, count);
                    count++;
                }
                // фиксируем максимальный оффсет обработанных записей
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий датчиков", e);
        } finally {
            try {
                log.debug("Фиксация смещений");
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        SensorsSnapshotAvro snapshot = record.value();

        log.info("Получен снапшот: {}", snapshot.toString());

        List<Scenario> scenarios = scenarioService.findByHubId(snapshot.getHubId());

        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorState();

        List<DeviceActionRequest> deviceActionRequests = scenarios.stream()
                .filter(scenario -> sensorStates.keySet().containsAll(scenario.getConditions().keySet()))
                .filter(scenario -> scenario.getConditions().keySet().stream()
                        .allMatch(sensorId -> snapshotService.isConditionMatch(sensorId, scenario.getConditions(), sensorStates)))
                .flatMap(scenario -> scenario.getActions().entrySet().stream()
                        .map(entry -> snapshotService.buildActionRequest(scenario, entry)))
                .toList();


        deviceActionRequests.forEach(actionRequest ->
                snapshotService.sendRequest(actionRequest, snapshot.getHubId())
        );
    }

    private void manageOffsets(
            Consumer<String, SensorsSnapshotAvro> consumer,
            ConsumerRecord<String, SensorsSnapshotAvro> record,
            int count
    ) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        // at-least-once семантика
        if (count % BATCH_SIZE == 0) {
            log.debug("Асинхронно фиксируем смещения обработанных сообщений");
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка при фиксации смещений: {}", offsets, exception);
                } else {
                    log.debug("Успешно зафиксированы смещения: {}", offsets);
                }
            });
        }
    }

}
