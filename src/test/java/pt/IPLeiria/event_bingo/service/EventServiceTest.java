package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.services.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    Event event =  new Event();

    @BeforeEach
    public void setUp() {
        event.setId(1);
        event.setPrediction("test");
        event.setStatus(EventStatus.Pending);
        event.setSport("TEST");
        event.setDate(new Date());
        event.setAway_team(null);
        event.setHome_team(null);

        var card = new Card();

        event.setCards(List.of(card));
    }

    @Test
    public void testDeleteEventFailCards(){
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Assertions.assertThrows(BadRequestException.class, () -> eventService.delete(0l));
    }
}
