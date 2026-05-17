package pt.IPLeiria.event_bingo.services;

import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.Transaction;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

import java.util.Date;
import java.util.List;

@Getter
@Service
public class CardCheckWinService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public CardCheckWinService(CardRepository cardRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void updateCardsAfterEventStatusChange(Event event) {
        List<Card> cards = cardRepository.findByEventsContaining(event);

        for (Card card : cards) {
            boolean hasPending = card.getEvents()
                    .stream()
                    .anyMatch(e -> e.getStatus() == EventStatus.Pending);

            if (hasPending) continue;

            evaluateCard(card);
        }
    }

    private void evaluateCard(Card card) {

        List<Event> events = card.getEvents();

        int rows = card.getRows();
        int cols = card.getCols();

        Event[][] grid = new Event[rows][cols];

        for (int i = 0; i < rows * cols && i < events.size(); i++) {
            grid[i / cols][i % cols] = events.get(i);
        }

        boolean hasLine = false;

        for (int i = 0; i < rows; i++) {
            boolean fullLine = true;

            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == null || grid[i][j].getStatus() != EventStatus.Win) {
                    fullLine = false;
                    break;
                }
            }

            if (fullLine) {
                hasLine = true;
                break;
            }
        }

        boolean bingo = events.stream()
                .allMatch(e -> e.getStatus() == EventStatus.Win);

        if (bingo || hasLine) {
            double amount = bingo? card.getBingo_prize(): card.getLine_prize();

            for (User user : card.getUsers()) {
                card.setTerminated(true);
                cardRepository.save(card);
                user.setBalance(user.getBalance() + amount);
                userRepository.save(user);

                var transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setDate(new Date());
                transaction.setType(TransactionType.PRIZE);
                transaction.setUser(user);

                transactionRepository.save(transaction);
            }
        }
    }
}
