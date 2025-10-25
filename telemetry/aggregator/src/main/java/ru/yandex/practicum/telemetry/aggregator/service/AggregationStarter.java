package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfiguration;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AggregationStarter {

    private final KafkaConfig kafka;

    private final KafkaConfiguration.KafkaSettings kafkaSettings;

    private final SnapshotService snapshotService;

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
                log.info("Получен сигнал на завершение работы");
                consumer.wakeup();
            }));

            consumer.subscribe(List.of(kafkaSettings.sensorTopic()));

            while(true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    log.info("Получено событие от датчика: {}", event.toString());

                    Optional<SensorsSnapshotAvro> snapshotOpt = snapshotService.updateState(event);

                    snapshotOpt.ifPresent(snapshot -> {
                        ProducerRecord<String, SensorsSnapshotAvro> message = new ProducerRecord<>(
                                kafkaSettings.snapshotsTopic(),
                                event.getHubId(),
                                snapshot
                        );

                        log.debug("Отправляем снапшот датчиков в кафку: {}", snapshot);

                        producer.send(message);
                    });

                    // at-least-once семантика
                    log.debug("Асинхронно фиксируем смещения обработанных сообщений");
                    consumer.commitAsync((offsets, exception) -> {
                        if(exception != null) {
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
                producer.flush();
                consumer.commitSync();
            } finally {
                kafka.stop();
            }
        }
    }
}
