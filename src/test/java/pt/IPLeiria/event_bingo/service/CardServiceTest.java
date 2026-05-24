package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.IPLeiria.event_bingo.dtos.cards.CardBuilderDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardPatchDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardRequestDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.CardService;
import pt.IPLeiria.event_bingo.services.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private Card card;
    private CardBuilderDto cardBuilderDto;
    private CardRequestDto cardRequestDto;
    private CardPatchDto  cardPatchDto;

    User user;

    @BeforeEach
    public void setUp() {
        Event ev1 = Event.builder()
                .id(0)
                .prediction("test")
                .date(new Date())
                .sport("test")
                .status(EventStatus.Pending)
                .build(),
            ev2 = Event.builder()
                    .id(1)
                    .prediction("test")
                    .date(new Date())
                    .sport("test")
                    .status(EventStatus.Pending)
                    .build(),
            ev3 = Event.builder()
                    .id(2)
                    .prediction("test")
                    .date(new Date())
                    .sport("test")
                    .status(EventStatus.Pending)
                    .build(),
            ev4 = Event.builder()
                    .id(3)
                    .prediction("test")
                    .date(new Date())
                    .sport("test")
                    .status(EventStatus.Pending)
                    .build();

        List<Event> events = List.of(ev1, ev2, ev3, ev4);

        card = new Card();
        card.setId(0);
        card.setName("test");
        card.setCols(2);
        card.setRows(2);
        card.setPrice(100);
        card.setEvents(events);
        card.setApproved(false);
        card.setTerminated(false);
        card.setLine_prize(50);
        card.setBingo_prize(500);
        card.setUsers(new ArrayList<>());


        cardBuilderDto = new CardBuilderDto(5,
                2, 2,
                50d, 500d,
                50d, 500d,
                10d, 200d);

        cardRequestDto = new CardRequestDto(card.getName(),
                card.getRows(),
                card.getCols(),
                card.getLine_prize(),
                card.getBingo_prize(),
                card.getPrice(),
                events.stream().map(x -> x.getId()).toList());

        cardPatchDto = new CardPatchDto(card.getName(),
                card.getRows(), card.getCols(),
                card.getLine_prize(),
                card.getBingo_prize(),
                card.getPrice(),
                events.stream().map(x -> x.getId()).toList(),
                card.isApproved());

        user = User.builder()
                .id(0)
                .full_name("test")
                .username("test")
                .email("a@mail.com")
                .password("test")
                .balance(0)
                .avatar(null)
                .cards(new ArrayList<>())
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    public  void testCardServiceCreate(){
        when(cardRepository.existsByEventsSignature(anyString())).thenReturn(false);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(card.getEvents().get(0)));
        when(cardRepository.save(any())).thenReturn(card);

        Assertions.assertDoesNotThrow(() -> cardService.create(cardRequestDto));
    }

    @Test
    public  void testCardServiceCreateFailSize(){
        cardRequestDto.setRows(10);
        Assertions.assertThrows(BadRequestException.class, () -> cardService.create(cardRequestDto));
    }

    @Test
    public  void testCardServiceCreateFailSignature(){
        when(cardRepository.existsByEventsSignature(anyString())).thenReturn(true);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(card.getEvents().get(0)));

        Assertions.assertThrows(BadRequestException.class, () -> cardService.create(cardRequestDto));
    }

    @Test
    public  void testCardServiceBuy(){
        user.setBalance(100);

        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(userService.get(anyLong())).thenReturn(user);

        when(userRepository.save(any())).thenReturn(user);
        when(cardRepository.save(any())).thenReturn(card);


        Assertions.assertDoesNotThrow(() -> cardService.buy(0L, "Test"));

        Assertions.assertEquals(user.getBalance(), (100 - card.getPrice()));
        Assertions.assertTrue(card.getUsers().contains(user));
        Assertions.assertTrue(user.getCards().contains(card));
    }

    @Test
    public  void testCardServiceBuyFailMoney(){
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(userService.get(anyLong())).thenReturn(user);

        Assertions.assertThrows(BadRequestException.class, () -> cardService.buy(0L, "Test"));
    }

    @Test
    public  void testCardServiceGenerate(){
        when(eventRepository.findEventsByStatus(any())).thenReturn(card.getEvents());
        when(cardRepository.existsByEventsSignature(anyString())).thenReturn(false);
        when(cardRepository.saveAll(any())).thenReturn(List.of(card));

        Assertions.assertDoesNotThrow(() -> cardService.generateCards(cardBuilderDto));
    }

    @Test
    public  void testCardServiceGenerateFailSizeMissMatch(){
        when(eventRepository.findEventsByStatus(any())).thenReturn(List.of());

        Assertions.assertThrows(BadRequestException.class, () -> cardService.generateCards(cardBuilderDto));
    }
}
