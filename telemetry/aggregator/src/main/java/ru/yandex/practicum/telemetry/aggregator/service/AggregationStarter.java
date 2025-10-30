package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfiguration;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AggregationStarter {

    private final KafkaConfig kafka;

    private final KafkaConfiguration.KafkaSettings kafkaSettings;

    private final SnapshotService snapshotService;

    private final Duration POLL_TIMEOUT = Duration.ofSeconds(1);

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final int BATCH_SIZE = 200;

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        final Consumer<String, SensorEventAvro> consumer = kafka.getConsumer(kafkaSettings.sensorConsumerGroupId());
        final Producer<String, SensorsSnapshotAvro> producer = kafka.getProducer();

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал на завершение работы в {}", AggregationStarter.class.getSimpleName());
                consumer.wakeup();
            }));

            consumer.subscribe(List.of(kafkaSettings.sensorTopic()));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(POLL_TIMEOUT);
                int count = 0;

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    handleRecord(record, producer);
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
                log.debug("Очистка буфера и фиксация смещений");
                producer.flush();
                consumer.commitSync();
            } finally {
                kafka.stop();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> record, Producer<String, SensorsSnapshotAvro> producer) {
        SensorEventAvro event = record.value();
        log.info("Получено событие от датчика: {}", event.toString());

        Optional<SensorsSnapshotAvro> snapshotOpt = snapshotService.updateState(event);

        if (snapshotOpt.isPresent()) {
            ProducerRecord<String, SensorsSnapshotAvro> message = new ProducerRecord<>(
                    kafkaSettings.snapshotsTopic(),
                    event.getHubId(),
                    snapshotOpt.get()
            );

            log.debug("Отправляем снапшот датчиков в кафку: {}", snapshotOpt.get());

            producer.send(message, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Ошибка при отправке сообщения в Kafka, topic: {}, key: {}",
                            kafkaSettings.snapshotsTopic(), event.getHubId(), exception);
                } else {
                    log.info("Сообщение успешно отправлено в Kafka, topic: {}, partition: {}, offset: {}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        }
    }

    private void manageOffsets(
            Consumer<String, SensorEventAvro> consumer,
            ConsumerRecord<String, SensorEventAvro> record,
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
