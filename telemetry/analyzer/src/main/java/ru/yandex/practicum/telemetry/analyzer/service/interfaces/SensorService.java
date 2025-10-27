package ru.yandex.practicum.telemetry.analyzer.service.interfaces;

import ru.yandex.practicum.telemetry.analyzer.dal.model.Sensor;

public interface SensorService {

    Sensor add(String sensorId, String hubId);

    void delete(String sensorId, String hubId);

}
