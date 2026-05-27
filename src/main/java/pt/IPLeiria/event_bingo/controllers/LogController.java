package pt.IPLeiria.event_bingo.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.IPLeiria.event_bingo.repositories.ApplicationLogRepository;

import java.util.List;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogController {

    private final ApplicationLogRepository applicationLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<?> getLogs() {
        return applicationLogRepository.findAll();
    }
}
