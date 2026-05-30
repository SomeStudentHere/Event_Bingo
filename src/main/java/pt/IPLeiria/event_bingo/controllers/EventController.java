package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.events.EventDto;
import pt.IPLeiria.event_bingo.dtos.events.EventPatchDto;
import pt.IPLeiria.event_bingo.dtos.events.EventRequestDto;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;
import pt.IPLeiria.event_bingo.mapper.EventMapper;
import pt.IPLeiria.event_bingo.services.EventService;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventMapper eventMapper;
    private final EventService eventService;
    private final LogBufferService logBufferService;
    private final ObjectMapper objectMapper;

    public EventController(EventMapper eventMapper, EventService eventService, LogBufferService logBufferService, ObjectMapper objectMapper) {
        this.eventMapper = eventMapper;
        this.eventService = eventService;
        this.logBufferService = logBufferService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getEvents(Pageable  pageable) {
        logBufferService.addLog(LogLevel.INFO, "List of Events requested");

        return ResponseEntity.ok(eventService.list(pageable).map(eventMapper::toDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id){
        logBufferService.addLog(LogLevel.INFO, "Event "+id+" requested");

        return ResponseEntity.ok(eventMapper.toDto(eventService.get(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventRequestDto request, UriComponentsBuilder uriBuilder){

        logBufferService.addLog(LogLevel.INFO, "Create event requested with data: " + objectMapper.writeValueAsString(request));

        var event = eventService.create(request);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.created(uriBuilder.path("/events/{id}").buildAndExpand(eventDto.getId()).toUri()).body(eventDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto request) {

        logBufferService.addLog(LogLevel.INFO, "Update event "+id+" requested with data: " + objectMapper.writeValueAsString(request));

        var event = eventService.update(request, id);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.ok(eventDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("{id}")
    public ResponseEntity<EventDto> patchEvent(@PathVariable Long id, @Valid @RequestBody EventPatchDto request) {

        logBufferService.addLog(LogLevel.INFO, "Patch event "+id+" requested with data: " + objectMapper.writeValueAsString(request));

        var event = eventService.patch(request, id);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.ok(eventDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){

        logBufferService.addLog(LogLevel.INFO, "Delete event "+id+" requested");

        eventService.delete(id);

        return ResponseEntity.ok().build();
    }
}
