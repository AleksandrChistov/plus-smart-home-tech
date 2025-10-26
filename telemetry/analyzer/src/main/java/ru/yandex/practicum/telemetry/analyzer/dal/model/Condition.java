package ru.yandex.practicum.telemetry.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ConditionType;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@ToString
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private ConditionType type;

    @Column(name = "operation", nullable = false)
    private ConditionOperation operation;

    private Integer value;
}
