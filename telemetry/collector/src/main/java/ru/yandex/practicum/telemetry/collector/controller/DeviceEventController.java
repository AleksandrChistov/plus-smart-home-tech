package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.controller.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaDeviceEventProducer;
import ru.yandex.practicum.telemetry.collector.service.mapper.hub.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.service.mapper.sensor.SensorEventMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class DeviceEventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final String HUB_TOPIC;
    private final String SENSOR_TOPIC;

    private final KafkaDeviceEventProducer kafkaProducer;
    private final Map<HubEventProto.PayloadCase, HubEventMapper> hubEventMappers;
    private final Map<SensorEventProto.PayloadCase, SensorEventMapper> sensorEventMappers;

    public DeviceEventController(
            @Value("${telemetry.collector.kafka.hub.topic}") String hubTopic, @Value("${telemetry.collector.kafka.sensor.topic}") String sensorTopic,
            KafkaDeviceEventProducer kafkaProducer, List<HubEventMapper> hubEventMappers, List<SensorEventMapper> sensorEventMappers
    ) {
        HUB_TOPIC = hubTopic;
        SENSOR_TOPIC = sensorTopic;

        this.kafkaProducer = kafkaProducer;

        this.hubEventMappers = hubEventMappers.stream()
                .collect(Collectors.toMap(HubEventMapper::getMessageType, Function.identity()));

        this.sensorEventMappers = sensorEventMappers.stream()
                .collect(Collectors.toMap(SensorEventMapper::getMessageType, Function.identity()));
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try  {
            log.info("HubEvent JSON: {}", request.toString());

            HubEventMapper hubEventMapper = Optional.of(hubEventMappers.get(request.getPayloadCase()))
                    .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип события для хаба"));

            HubEventAvro hubEventAvro = hubEventMapper.map(request);

            kafkaProducer.send(HUB_TOPIC, hubEventAvro.getTimestamp().toEpochMilli(), hubEventAvro.getHubId(), hubEventAvro);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при отправке события HubEvent", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e.getCause())
            ));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try  {
            log.info("SensorEvent JSON: {}", request.toString());

            SensorEventMapper sensorEventMapper = Optional.of(sensorEventMappers.get(request.getPayloadCase()))
                    .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип события для датчика"));

            SensorEventAvro sensorEventAvro = sensorEventMapper.map(request);

            kafkaProducer.send(SENSOR_TOPIC, sensorEventAvro.getTimestamp().toEpochMilli(), sensorEventAvro.getHubId(), sensorEventAvro);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при отправке события SensorEvent", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e.getCause())
            ));
        }
    }

}
