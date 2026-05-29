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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import pt.IPLeiria.event_bingo.services.LogBufferService;

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

    //cardservice usa isto internamente
    @Mock
    private LogBufferService logBufferService;


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
                .cards(new ArrayList<>())
                .build();

        User user2 = User.builder()
                .balance(100)
                .status(UserStatus.ACTIVE)
                .cards(new ArrayList<>())
                .build();

        User user3 = User.builder()
                .balance(100)
                .status(UserStatus.ACTIVE)
                .cards(new ArrayList<>())
                .build();

        Card card = Card.builder()
                .rows(2)
                .cols(2)
                .line_prize(50)
                .bingo_prize(500)
                .events(List.of(event1, event2, event3, event4))
                .users(new ArrayList<>(List.of(user, user2)))
                .terminated(false)
                .build();

        event1.setStatus(EventStatus.Win);
        event2.setStatus(EventStatus.Win);

        event3.setStatus(EventStatus.Lose);
        event4.setStatus(EventStatus.Lose);

        when(cardRepository.findByEventsContaining(event1))
                .thenReturn(List.of(card));

        cardCheckWinService.updateCardsAfterEventStatusChange(event1);

        assertTrue(card.isTerminated());

        assertEquals(150, user.getBalance());
        assertEquals(150, user2.getBalance());
        assertEquals(100, user3.getBalance());

        verify(cardRepository).save(card);

        verify(userRepository, times(2)).save(any(User.class));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository, times(2)).save(captor.capture());

        List<Transaction> transactions = captor.getAllValues();

        assertEquals(2, transactions.size());

        for (Transaction transaction : transactions) {
            assertEquals(TransactionType.PRIZE, transaction.getType());
            assertEquals(50, transaction.getAmount());
            assertNotNull(transaction.getDate());
        }

        List<User> transactionUsers = transactions.stream()
                .map(Transaction::getUser)
                .toList();

        assertTrue(transactionUsers.contains(user));
        assertTrue(transactionUsers.contains(user2));
        assertFalse(transactionUsers.contains(user3));
    }

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
                .cards(new ArrayList<>())
                .build();

        User user2 = User.builder()
                .balance(100)
                .status(UserStatus.ACTIVE)
                .cards(new ArrayList<>())
                .build();

        User user3 = User.builder()
                .balance(100)
                .status(UserStatus.ACTIVE)
                .cards(new ArrayList<>())
                .build();

        Card card = Card.builder()
                .rows(2)
                .cols(2)
                .line_prize(50)
                .bingo_prize(500)
                .events(List.of(event1, event2, event3, event4))
                .users(new ArrayList<>(List.of(user, user2)))
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
        assertEquals(600, user2.getBalance());
        assertEquals(100, user3.getBalance());

        verify(cardRepository).save(card);

        verify(userRepository, times(2)).save(any(User.class));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository, times(2)).save(captor.capture());

        List<Transaction> transactions = captor.getAllValues();

        assertEquals(2, transactions.size());

        for (Transaction transaction : transactions) {
            assertEquals(TransactionType.PRIZE, transaction.getType());
            assertEquals(500, transaction.getAmount());
            assertNotNull(transaction.getDate());
        }

        List<User> transactionUsers = transactions.stream()
                .map(Transaction::getUser)
                .toList();

        assertTrue(transactionUsers.contains(user));
        assertTrue(transactionUsers.contains(user2));
        assertFalse(transactionUsers.contains(user3));
    }

}
