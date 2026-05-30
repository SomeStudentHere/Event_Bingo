package pt.IPLeiria.event_bingo.services;

import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.entities.ApplicationLog;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogBufferService {
    private final List<ApplicationLog> buffer = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(LogBufferService.class);

    @Synchronized
    public void addLog(LogLevel level, String message) {
        var log = new ApplicationLog();

        log.setLevel(level);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());

        buffer.add(log);

        switch (level) {
            case INFO:
                logger.info(message);
                break;

            case WARNING:
                logger.warn(message);
                break;

            case ERROR:
                logger.error(message);
                break;
        }
    }


    public synchronized List<ApplicationLog> drainLogs() {
        if (buffer.isEmpty()) return List.of();

        List<ApplicationLog> copy = new ArrayList<>(buffer);
        buffer.clear();

        return copy;
    }
}
