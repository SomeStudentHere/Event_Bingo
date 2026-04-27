package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByEventsContaining(Event event);

    boolean existsByEventsSignature(String eventsSignature);

    List<Card> findCardsByApprovedIs(boolean approved);
}
