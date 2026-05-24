package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.Transaction;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.services.CardCheckWinService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
public class CardCheckWinServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CardCheckWinService cardCheckWinService;

    @Test
    public void testUpdateCardsAfterEventStatusChangePending() {
        Event event1 = new Event();
        event1.setStatus(EventStatus.Pending);

        Event event2 = new Event();
        event2.setStatus(EventStatus.Pending);

        Card card = Card.builder()
                .rows(1)
                .cols(1)
                .line_prize(50)
                .bingo_prize(100)
                .events(List.of(event1, event2))
                .terminated(false)
                .build();

        when(cardRepository.findByEventsContaining(event1)).thenReturn(List.of(card));

        event1.setStatus(EventStatus.Win);

        cardCheckWinService.updateCardsAfterEventStatusChange(event1);

        verify(cardRepository, never()).save(any(Card.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testUpdateCardsAfterEventStatusChangeLineWin() {
        Event event1 = new Event();
        event1.setStatus(EventStatus.Pending);

        Event event2 = new Event();
        event2.setStatus(EventStatus.Pending);

        Event event3 = new Event();
        event3.setStatus(EventStatus.Pending);

        Event event4 = new Event();
        event4.setStatus(EventStatus.Pending);

        User user = User.builder()
                .balance(100)
                .status(UserStatus.ACTIVE)
                .build();

        Card card = Card.builder()
                .rows(2)
                .cols(2)
                .line_prize(50)
                .bingo_prize(500)
                .events(List.of(event1, event2, event3, event4))
                .users(List.of(user))
                .terminated(false)
                .build();


        event1.setStatus(EventStatus.Win);
        event2.setStatus(EventStatus.Win);

        event3.setStatus(EventStatus.Lose);
        event4.setStatus(EventStatus.Lose);

        when(cardRepository.findByEventsContaining(event1)).thenReturn(List.of(card));
        cardCheckWinService.updateCardsAfterEventStatusChange(event1);

        assertTrue(card.isTerminated());

        assertEquals(150, user.getBalance());

        verify(cardRepository).save(card);
        verify(userRepository).save(user);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(TransactionType.PRIZE, transaction.getType());
        assertEquals(50, transaction.getAmount());
        assertEquals(user, transaction.getUser());
        assertNotNull(transaction.getDate());    }

    @Test
    public void testUpdateCardsAfterEventStatusChangeBingo() {
        Event event1 = new Event();
        event1.setStatus(EventStatus.Pending);

        Event event2 = new Event();
        event2.setStatus(EventStatus.Pending);

        Event event3 = new Event();
        event3.setStatus(EventStatus.Pending);

        Event event4 = new Event();
        event4.setStatus(EventStatus.Pending);

        User user = User.builder()
                .balance(100)
                .status(UserStatus.ACTIVE)
                .build();

        Card card = Card.builder()
                .rows(2)
                .cols(2)
                .line_prize(50)
                .bingo_prize(500)
                .events(List.of(event1, event2, event3, event4))
                .users(List.of(user))
                .terminated(false)
                .build();


        event1.setStatus(EventStatus.Win);
        event2.setStatus(EventStatus.Win);
        event3.setStatus(EventStatus.Win);
        event4.setStatus(EventStatus.Win);

        when(cardRepository.findByEventsContaining(event1)).thenReturn(List.of(card));
        cardCheckWinService.updateCardsAfterEventStatusChange(event1);

        assertTrue(card.isTerminated());

        assertEquals(600, user.getBalance());

        verify(cardRepository).save(card);
        verify(userRepository).save(user);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(TransactionType.PRIZE, transaction.getType());
        assertEquals(500, transaction.getAmount());
        assertEquals(user, transaction.getUser());
        assertNotNull(transaction.getDate());
    }
}
