package ru.yandex.practicum.telemetry.analyzer.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
