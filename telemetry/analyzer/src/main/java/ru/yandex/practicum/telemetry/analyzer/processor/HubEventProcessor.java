package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.kafka.KafkaFactory;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.ScenarioService;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.SensorService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventProcessor {

    private final KafkaFactory kafkaFactory;

    private final SensorService sensorService;

    private final ScenarioService scenarioService;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void run() {
       try {
           final Consumer<String, HubEventAvro> consumer = kafkaFactory.createConsumer(HubEventProcessor.class.getSimpleName());
           final Duration pollTimeout = Duration.ofMillis(kafkaFactory.getPollTimeout(HubEventProcessor.class.getSimpleName()));

           try {
               Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                   log.info("Получен сигнал на завершение работы в {}", HubEventProcessor.class.getSimpleName());
                   consumer.wakeup();
               }));

               consumer.subscribe(kafkaFactory.getTopics(HubEventProcessor.class.getSimpleName()));

               while (true) {
                   ConsumerRecords<String, HubEventAvro> records = consumer.poll(pollTimeout);

                   for (ConsumerRecord<String, HubEventAvro> record : records) {
                       handleRecord(record);
                       // at-least-once семантика
                       currentOffsets.put(
                               new TopicPartition(record.topic(), record.partition()),
                               new OffsetAndMetadata(record.offset() + 1)
                       );
                       consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                           if (exception != null) {
                               log.warn("Во время фиксации произошла ошибка. Cмещения: {}", offsets, exception);
                           } else {
                               log.debug("Успешно зафиксированы смещения: {}", offsets);
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
                   log.debug("Фиксация смещений");
                   consumer.commitSync();
               } finally {
                   consumer.close();
               }
           }
       } catch (Exception e) {
           log.error("Ошибка создания консьюмера в {}", HubEventProcessor.class.getSimpleName(), e);
       }
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record) {
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
    }

}


