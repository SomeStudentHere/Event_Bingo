package pt.IPLeiria.event_bingo.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.IPLeiria.event_bingo.repositories.ApplicationLogRepository;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogController {

    private final ApplicationLogRepository applicationLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<?> getLogs(Pageable pageable) {
        return applicationLogRepository.findAll(pageable);
    }
}
