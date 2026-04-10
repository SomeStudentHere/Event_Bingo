package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.IPLeiria.event_bingo.entities.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByEventsSignature(String eventsSignature);
}
