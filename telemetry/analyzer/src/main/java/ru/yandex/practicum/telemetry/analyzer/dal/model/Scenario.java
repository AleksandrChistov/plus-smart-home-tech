package ru.yandex.practicum.telemetry.analyzer.dal.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "scenarios")
@Getter
@Setter
@ToString(exclude = {"conditions", "actions"})
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @BatchSize(size = 100)
    @MapKeyColumn(
            table="scenarios_conditions",
            name="sensor_id"
    )
    @JoinTable(
            name="scenarios_conditions",
            joinColumns=@JoinColumn(name="scenario_id"),
            inverseJoinColumns=@JoinColumn(name="condition_id")
    )
    private Map<String, Condition> conditions = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @BatchSize(size = 100)
    @MapKeyColumn(
            table="scenario_actions",
            name="sensor_id"
    )
    @JoinTable(
            name="scenarios_conditions",
            joinColumns=@JoinColumn(name="scenario_id"),
            inverseJoinColumns=@JoinColumn(name="action_id")
    )
    private Map<String, Action> actions = new HashMap<>();
}
