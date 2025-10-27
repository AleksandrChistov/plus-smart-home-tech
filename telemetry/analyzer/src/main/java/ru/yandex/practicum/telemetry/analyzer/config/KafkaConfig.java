package ru.yandex.practicum.telemetry.analyzer.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties("telemetry.analyzer.kafka")
@Getter
@Setter
public class KafkaConfig {
    private Properties properties;
    private List<KafkaConsumerConfig> consumers;
    private Map<String, KafkaConsumerConfig> consumerConfigByType;

    @PostConstruct
    public void init() {
        consumerConfigByType = consumers.stream()
                .collect(Collectors.toMap(KafkaConsumerConfig::getType, Function.identity()));
    }

    public KafkaConsumerConfig getConsumerConfig(String consumerType) {
        return Optional.ofNullable(consumerConfigByType.get(consumerType))
                .orElseThrow(() -> new IllegalArgumentException("Тип косьюмера " + consumerType + " не найден"));
    }

}
