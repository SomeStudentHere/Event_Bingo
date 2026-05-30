package pt.IPLeiria.event_bingo.component;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.IPLeiria.event_bingo.entities.ApplicationLog;
import pt.IPLeiria.event_bingo.repositories.ApplicationLogRepository;
import pt.IPLeiria.event_bingo.services.LogBufferService;

import java.util.List;

@Component
@AllArgsConstructor
public class LogScheduler {
    private final LogBufferService logBufferService;
    private final ApplicationLogRepository repository;

    @Scheduled(fixedRate = 5000)
    public void drainLogs() {

        List<ApplicationLog> logs = logBufferService.drainLogs();

        if (!logs.isEmpty()) {
            repository.saveAll(logs);

            System.out.println("Saved " + logs.size() + " logs to database");
        }
    }
}
