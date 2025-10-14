package ru.yandex.practicum.telemetry.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@Slf4j
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
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, eventTimestamp, key, data);
        Future<RecordMetadata> futureResult = producer.send(record);
        producer.flush();
        String eventName = data != null ? data.getClass().getSimpleName() : "null";
        try {
            RecordMetadata metadata = futureResult.get();
            log.info("Событие {} было успешно сохранено в топик {}, в партицию {}, со смещением {}, ключ '{}', timestamp {}",
                    eventName,
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    key,
                    eventTimestamp
            );
        } catch (InterruptedException | ExecutionException e) {
            producer.close(Duration.ofSeconds(10));
            log.warn("Не удалось записать событие {} в топик {}", eventName, topic, e);
        }
    }
}
