package ru.yandex.practicum.telemetry.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@ToString
public class Sensor {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;
}
