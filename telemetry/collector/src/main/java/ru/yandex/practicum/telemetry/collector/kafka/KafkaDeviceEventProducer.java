package ru.yandex.practicum.telemetry.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;

import java.time.Duration;
import java.util.Properties;

@Component
public class KafkaDeviceEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaDeviceEventProducer(@Value("${telemetry.collector.kafka.server}") String kafkaServer) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        producer = new KafkaProducer<>(config);
    }

    public void send(String topic, Long eventTimestamp, String key, SpecificRecordBase data) {
        try {
            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, eventTimestamp, key, data);
            producer.send(record);
            producer.flush();
        } catch (Exception e) {
            producer.flush();
            producer.close(Duration.ofSeconds(10));
            String errorMessage = String.format(
                    "Failed to send message to Kafka topic [%s] with key '%s' and timestamp %d. " +
                            "Record data type: %s. Original exception: %s",
                    topic,
                    key,
                    eventTimestamp,
                    data != null ? data.getClass().getSimpleName() : "null",
                    e.getMessage()
            );
            throw new RuntimeException(errorMessage, e);
        }
    }
}
