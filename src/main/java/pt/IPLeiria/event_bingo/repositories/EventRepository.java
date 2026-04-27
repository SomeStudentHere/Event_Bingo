package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findEventsByStatus(EventStatus status);
}
