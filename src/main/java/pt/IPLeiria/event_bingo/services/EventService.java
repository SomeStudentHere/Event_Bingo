package pt.IPLeiria.event_bingo.services;

import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.events.EventPatchDto;
import pt.IPLeiria.event_bingo.dtos.events.EventRequestDto;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.EventMapper;
import pt.IPLeiria.event_bingo.repositories.EventRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CardCheckWinService cardCheckWinService;

    public EventService(EventRepository eventRepository, EventMapper eventMapper, CardCheckWinService cardCheckWinService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.cardCheckWinService = cardCheckWinService;
    }

    public Event get(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + " not Found"));
    }

    public List<Event> list() {
        return eventRepository.findAll();
    }

    public Event create(EventRequestDto request){
        Event event = eventMapper.toEntity(request);

        event.setStatus(EventStatus.Pending);

        eventRepository.save(event);

        return event;
    }

    public Event update(EventRequestDto request, Long id){
        var event = get(id);

        event.setSport(request.getSport());
        event.setDate(request.getDate());
        event.setPrediction(request.getPrediction());
        event.setAway_team(request.getAway_team());
        event.setHome_team(request.getHome_team());

        eventRepository.save(event);

        return event;
    }

    public Event patch(EventPatchDto request, Long id){
        var event = get(id);

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
        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
            cardCheckWinService.updateCardsAfterEventStatusChange(event);
        }

        eventRepository.save(event);
        return event;
    }

    public void delete(Long id){
        var event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event id not found: " + id));

        if (!event.getCards().isEmpty()) throw new BadRequestException("Can't delete event " + id + " because it's associated to an card!");

        eventRepository.delete(event);
    }
}
