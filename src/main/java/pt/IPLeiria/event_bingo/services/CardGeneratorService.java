package pt.IPLeiria.event_bingo.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.cards.CardBuilderDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardRequestDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Service
public class CardGeneratorService {

    private boolean running = false;
    private Double progress = null;

    private synchronized void update(Boolean running, Double progress){
        this.running = running;
        this.progress = progress;
    }


    //problema (já resolvido)
    //inicialmente tava a passar events em vez de eventsId
    //Erro por tar a passar entities e fazer com q fique desync

    //esqueci me de @Transactional por isso os repositories n funcionavam

    @Async
    @Transactional
    public void generateCards(CardBuilderDto request, List<Long> eventsIds, CardRepository cardRepository, EventRepository eventRepository){
        update(true, 0.0);

        var shuffled = new ArrayList<>(eventsIds);

        Random rand = new Random();

        ArrayList<Card> cards = new ArrayList<>();
        while (cards.size() < request.getCount()){

            int tries = 0;

            var card = new Card();

            card.setApproved(false);
            card.setName("<To edit>");
            card.setCols(request.getCols());
            card.setRows(request.getRows());
            card.setPrice(rand.nextDouble(request.getPrice_max() - request.getPrice_min() + 1) + request.getPrice_min());
            card.setLine_prize(rand.nextDouble(request.getLine_prize_max() - request.getLine_prize_min() + 1) + request.getLine_prize_min());
            card.setBingo_prize(rand.nextDouble(request.getBingo_prize_max() - request.getBingo_prize_min() + 1) + request.getBingo_prize_min());

            do {
                Collections.shuffle(shuffled);
                card.setEvents(shuffled.stream().limit((long) request.getCols() * request.getRows()).collect(Collectors.toList()), eventRepository);
                tries++;
                if (tries > 5) break;
            } while (cardRepository.existsByEventsSignature(card.getEventsSignature()));

            if (tries > 5) break;

            cards.add(card);
            update(true, ((double)cards.size())/request.getCount());

            System.out.printf("Card created with signature: " + card.getEventsSignature());
        }

        if (!cards.isEmpty())
            cardRepository.saveAll(cards);

        update(false, null);
    }
}
