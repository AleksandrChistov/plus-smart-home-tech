package ru.yandex.practicum.telemetry.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConfig;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConsumerConfig;

import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class KafkaFactory {

    private final KafkaConfig kafkaConfig;

    public <K, V> Consumer<K, V> createConsumer(String consumerType) {
        KafkaConsumerConfig consumerConfig = kafkaConfig.getConsumerConfig(consumerType);

        Properties properties = new Properties();

        properties.putAll(kafkaConfig.getProperties());
        properties.putAll(consumerConfig.getProperties());

        return new KafkaConsumer<>(properties);
    }

    public List<String> getTopics(String consumerType) {
        return kafkaConfig.getConsumerConfig(consumerType).getTopics();
    }

    public Long getPollTimeout(String consumerType) {
        return kafkaConfig.getConsumerConfig(consumerType).getPollTimeout();
    }
}
