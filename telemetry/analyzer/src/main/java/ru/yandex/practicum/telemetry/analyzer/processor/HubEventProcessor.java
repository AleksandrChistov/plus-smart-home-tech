package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.kafka.KafkaFactory;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.ScenarioService;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.SensorService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventProcessor implements Runnable {

    private final KafkaFactory kafkaFactory;

    private final SensorService sensorService;

    private final ScenarioService scenarioService;

    @Override
    public void run() {
        final Consumer<String, HubEventAvro> consumer = kafkaFactory.createConsumer(HubEventProcessor.class.getSimpleName());

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал на завершение работы в {}", HubEventProcessor.class.getSimpleName());
                consumer.wakeup();
            }));

            consumer.subscribe(kafkaFactory.getTopics(HubEventProcessor.class.getSimpleName()));

            while(true) {
                Long pollTimeout = kafkaFactory.getPollTimeout(HubEventProcessor.class.getSimpleName());

                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();

                    log.info("Получено событие от устройства: {}", event.toString());

                    Object payload = event.getPayload();

                    switch (payload) {
                        case DeviceAddedEventAvro deviceAddedEvent -> {
                            log.info("Сохраняем новое устройство: {}", deviceAddedEvent);
                            sensorService.add(deviceAddedEvent.getId(), event.getHubId());
                        }
                        case DeviceRemovedEventAvro deviceRemovedEvent -> {
                            log.info("Удаляем устройство: {}", deviceRemovedEvent);
                            sensorService.delete(deviceRemovedEvent.getId(), event.getHubId());
                        }
                        case ScenarioAddedEventAvro scenarioAddedEvent -> {
                            log.info("Добавляем новый сценарий: {}", scenarioAddedEvent);
                            scenarioService.add(scenarioAddedEvent, event.getHubId());
                        }
                        case ScenarioRemovedEventAvro scenarioRemovedEvent -> {
                            log.info("Удаляем сценарий: {}", scenarioRemovedEvent);
                            scenarioService.delete(scenarioRemovedEvent, event.getHubId());
                        }
                        default -> throw new IllegalArgumentException("Неизвестный тип события");
                    }

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
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

}


