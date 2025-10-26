package ru.yandex.practicum.telemetry.analyzer.dal.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Properties;

@Getter
@Setter
public class KafkaConsumerConfig {
    private String type;
    private Properties properties;
    private List<String> topics;
    private Long pollTimeout;
}
