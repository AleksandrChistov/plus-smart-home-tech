package ru.yandex.practicum.telemetry.analyzer.dal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Configuration
@ConfigurationProperties("telemetry.analyzer.kafka")
@Getter
@Setter
public class KafkaConfig {
    private Properties properties;
    private List<KafkaConsumerConfig> consumers;

    public Optional<KafkaConsumerConfig> getConsumerConfig(String type) {
        return consumers.stream()
                .filter(consumer -> type.equals(consumer.getType()))
                .findFirst();
    }

}
