package pt.IPLeiria.event_bingo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.EventDto;
import pt.IPLeiria.event_bingo.dtos.EventRequestDto;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.mapper.EventMapper;
import pt.IPLeiria.event_bingo.repositories.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventController(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @GetMapping
    public List<EventDto> getEvents(){
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }


    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventRequestDto request, UriComponentsBuilder uriBuilder){

        Event event = eventMapper.toEntity(request);

        event.setStatus(EventStatus.Pending);

        eventRepository.save(event);

        var eventDto = eventMapper.toDto(event);

        return ResponseEntity.created(uriBuilder.path("/events/{id}").buildAndExpand(eventDto.getId()).toUri()).body(eventDto);
    }
}
