package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SnapshotServiceImpl implements SnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                event.getHubId(),
                hubId -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setTimestamp(Instant.now())
                        .setSensorState(new HashMap<>())
                        .build()
        );

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorState();

        if (sensorsState.containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsState.get(event.getId());

            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || !isSnapshotChanged(oldState.getData(), event.getPayload())) {
                log.debug("Событие не изменилось");
                return Optional.empty();
            } else {
                sensorsState.put(event.getId(), createNewState(event));
                snapshot.setSensorState(sensorsState);
                snapshot.setTimestamp(Instant.now());

                log.debug("Снапшот был обновлен");
                return Optional.of(snapshot);
            }
        } else {
            sensorsState.put(event.getId(), createNewState(event));
            snapshot.setSensorState(sensorsState);
            snapshot.setTimestamp(Instant.now());

            log.debug("Снапшот был обновлен");
            return Optional.of(snapshot);
        }
    }

    private SensorStateAvro createNewState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }

    private boolean isSnapshotChanged(Object oldState, Object newState) {
        if (!oldState.getClass().equals(newState.getClass())) {
            log.warn("Типы событий отличаются:  {} != {}", oldState.getClass(), newState.getClass());
            return false;
        }

        return switch (oldState) {
            case ClimateSensorAvro oldClimate when newState instanceof ClimateSensorAvro newClimate -> {
                log.info("Изменения в климатическом датчике: {} != {}", oldClimate, newClimate);
                yield oldClimate.getTemperatureC() != newClimate.getTemperatureC() ||
                        oldClimate.getHumidity() != newClimate.getHumidity() ||
                        oldClimate.getCo2Level() != newClimate.getCo2Level();
            }
            case LightSensorAvro oldLight when newState instanceof LightSensorAvro newLight -> {
                log.info("Изменения в датчике света: {} != {}", oldLight, newLight);
                yield oldLight.getLinkQuality() != newLight.getLinkQuality() ||
                        oldLight.getLuminosity() != newLight.getLuminosity();
            }
            case MotionSensorAvro oldMotion when newState instanceof MotionSensorAvro newMotion -> {
                log.info("Изменения в датчике движения: {} != {}", oldMotion, newMotion);
                yield oldMotion.getLinkQuality() != newMotion.getLinkQuality() ||
                        oldMotion.getMotion() != newMotion.getMotion() ||
                        oldMotion.getVoltage() != newMotion.getVoltage();
            }
            case SwitchSensorAvro oldSwitch when newState instanceof SwitchSensorAvro newSwitch -> {
                log.info("Изменения в датчике переключения: {} != {}", oldSwitch, newSwitch);
                yield oldSwitch.getState() != newSwitch.getState();
            }
            case TemperatureSensorAvro oldTemp when newState instanceof TemperatureSensorAvro newTemp -> {
                log.info("Изменения в датчике температуры: {} != {}", oldTemp, newTemp);
                yield oldTemp.getTemperatureC() != newTemp.getTemperatureC() ||
                        oldTemp.getTemperatureF() != newTemp.getTemperatureF();
            }
            default -> {
                log.warn("Неизвестный тип события: {}", oldState.getClass());
                yield false;
            }
        };
    }
}