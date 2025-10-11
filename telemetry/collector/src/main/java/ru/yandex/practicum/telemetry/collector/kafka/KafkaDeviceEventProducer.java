package ru.yandex.practicum.telemetry.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Component
public class KafkaDeviceEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaDeviceEventProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        producer = new KafkaProducer<>(config);
    }

    public void send(String topic, SpecificRecordBase data) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, data);
        producer.send(record);
    }
}
