package ru.yandex.practicum.telemetry.aggregator.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;


public interface KafkaConfig {

    Consumer<String, SensorEventAvro> getConsumer(String groupId);

    Producer<String, SensorsSnapshotAvro> getProducer();

    void stop();

}
