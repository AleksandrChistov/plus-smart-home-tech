package ru.yandex.practicum.telemetry.analyzer.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
