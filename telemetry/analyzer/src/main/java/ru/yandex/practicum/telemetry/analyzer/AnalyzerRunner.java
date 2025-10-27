package ru.yandex.practicum.telemetry.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.processor.HubEventProcessor;
import ru.yandex.practicum.telemetry.analyzer.processor.SnapshotProcessor;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnalyzerRunner {

    private final HubEventProcessor hubEventProcessor;

    private final SnapshotProcessor snapshotProcessor;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            log.info("Запускаем в отдельном потоке обработчик событий от пользовательских хабов");
            Thread hubEventsThread = new Thread(hubEventProcessor);
            hubEventsThread.setName("HubEventHandlerThread");
            hubEventsThread.start();

            log.info("В текущем потоке начинаем обработку снимков состояния датчиков");
            snapshotProcessor.process();
        } catch (Exception e) {
            log.error("Ошибка запуска обработчиков в AnalyzerRunner", e);
        }
    }
}
