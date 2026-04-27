package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.events.EventDto;
import pt.IPLeiria.event_bingo.dtos.events.EventPatchDto;
import pt.IPLeiria.event_bingo.dtos.events.EventRequestDto;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.EventMapper;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.services.CardCheckWinService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CardCheckWinService cardCheckWinService;

    public EventController(EventRepository eventRepository, EventMapper eventMapper, CardCheckWinService cardCheckWinService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.cardCheckWinService = cardCheckWinService;

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

    @PutMapping("{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto request) {

        var event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + " not Found"));

        event.setSport(request.getSport());
        event.setDate(request.getDate());
        event.setPrediction(request.getPrediction());
        event.setAway_team(request.getAway_team());
        event.setHome_team(request.getHome_team());

        eventRepository.save(event);

        var eventDto = eventMapper.toDto(event);
        return ResponseEntity.ok(eventDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<EventDto> patchEvent(@PathVariable Long id, @Valid @RequestBody EventPatchDto request) {

        var event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + " not Found"));

        if (request.getSport() != null)
            event.setSport(request.getSport());
        if (request.getDate() != null)
            event.setDate(request.getDate());
        if (request.getPrediction() != null)
            event.setPrediction(request.getPrediction());
        if (request.getAway_team() != null)
            event.setAway_team(request.getAway_team());
        if (request.getHome_team() != null)
            event.setHome_team(request.getHome_team());
        if (request.getStatus() != null)
            event.setStatus(request.getStatus());
            cardCheckWinService.updateCardsAfterEventStatusChange(event);

        eventRepository.save(event);

        var eventDto = eventMapper.toDto(event);
        return ResponseEntity.ok(eventDto);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){
        var event = eventRepository.findById(id).orElse(null);

        if (event.getCards().size() > 0) throw new BadRequestException("Can't delete event " + id + " because it's associated to an card!");

        if (event == null) return ResponseEntity.notFound().build();

        eventRepository.delete(event);

        return ResponseEntity.ok().build();
    }
}
