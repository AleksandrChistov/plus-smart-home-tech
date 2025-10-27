package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.telemetry.analyzer.dal.dao.SensorRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.service.interfaces.SensorService;

@Service
@RequiredArgsConstructor
@Transactional
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    @Override
    public Sensor add(String sensorId, String hubId) {
        return sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseGet(() -> sensorRepository.save(new Sensor(sensorId, hubId)));
    }

    @Override
    public void delete(String sensorId, String hubId) {
        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .ifPresent(sensorRepository::delete);
    }
}
