package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.events.EventDto;
import pt.IPLeiria.event_bingo.dtos.events.EventPatchDto;
import pt.IPLeiria.event_bingo.dtos.events.EventRequestDto;
import pt.IPLeiria.event_bingo.mapper.EventMapper;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventService eventService;

    public EventController(EventRepository eventRepository, EventMapper eventMapper, EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDto> getEvents(){
        return eventService.list()
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventRequestDto request, UriComponentsBuilder uriBuilder){

        var event = eventService.create(request);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.created(uriBuilder.path("/events/{id}").buildAndExpand(eventDto.getId()).toUri()).body(eventDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto request) {

        var event = eventService.update(request, id);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.ok(eventDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<EventDto> patchEvent(@PathVariable Long id, @Valid @RequestBody EventPatchDto request) {

        var event = eventService.patch(request, id);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.ok(eventDto);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){

        eventService.delete(id);

        return ResponseEntity.ok().build();
    }
}
