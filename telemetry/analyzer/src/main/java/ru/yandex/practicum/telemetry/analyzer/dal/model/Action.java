package ru.yandex.practicum.telemetry.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.telemetry.analyzer.dal.enums.ActionType;

@Entity
@Table(name = "actions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType type;

    @Column(name = "value")
    private Integer value;
}
