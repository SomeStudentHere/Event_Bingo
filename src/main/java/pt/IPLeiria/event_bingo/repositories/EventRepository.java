package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.IPLeiria.event_bingo.entities.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
