package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.yandex.practicum.kafka.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaConfiguration {
    @Value("${telemetry.aggregator.kafka.sensor.consumer.groupId}") String sensorConsumerGroupId;

    @Value("${telemetry.aggregator.kafka.sensor.topic}") String sensorTopic;

    @Value("${telemetry.aggregator.kafka.snapshot.topic}") String snapshotsTopic;

    @Value("${telemetry.aggregator.kafka.server}") String server;

    public record KafkaSettings(String sensorConsumerGroupId, String sensorTopic, String snapshotsTopic) {}

    @Bean
    public KafkaSettings kafkaSettings() {
        return new KafkaSettings(sensorConsumerGroupId, sensorTopic, snapshotsTopic);
    }

    @Bean
    @Scope("prototype")
    public KafkaConfig kafkaConfig() {
        return new KafkaConfig() {
            private Consumer<String, SensorEventAvro> consumer;

            private Producer<String, SensorsSnapshotAvro> producer;

            @Override
            public Consumer<String, SensorEventAvro> getConsumer(String groupId) {
                if (consumer == null) {
                    initConsumer(groupId);
                    log.info("Создали косьюмер с groupId = {}", groupId);
                }
                return consumer;
            }

            private void initConsumer(String groupId) {
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
                config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
                consumer = new KafkaConsumer<>(config);
            }

            @Override
            public Producer<String, SensorsSnapshotAvro> getProducer() {
                if (producer == null) {
                    initProducer();
                    log.info("Создали продюсер");
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
                producer = new KafkaProducer<>(config);
            }

            @Override
            public void stop() {
                if (consumer != null) {
                    log.info("Закрываем косьюмер");
                    consumer.close();
                }
                if (producer != null) {
                    log.info("Закрываем продюсер");
                    producer.close();
                }
            }
        };
    }
}
