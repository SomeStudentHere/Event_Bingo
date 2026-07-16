package pt.IPLeiria.event_bingo.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.cards.CardBuilderDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardPatchDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardRequestDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Transaction;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.CardMapper;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Service
public class CardService {

    private final JwtService jwtService;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final LogBufferService logBufferService;
    private boolean running = false;
    private Double progress = null;

    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CardService(CardMapper cardMapper, CardRepository cardRepository, EventRepository eventRepository, UserRepository userRepository, JwtService jwtService, TransactionRepository transactionRepository, UserService userService, LogBufferService logBufferService) {
        this.cardMapper = cardMapper;
        this.cardRepository = cardRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.logBufferService = logBufferService;
    }

    public Card get(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card " + id + " not Found"));
    }

    public Page<Card> list(User user, Pageable pageable) {

        if (user != null && user.getRole() == UserRole.ADMIN) {
            return cardRepository.findAll(pageable);
        }

        return cardRepository.findCardsByApprovedIs(true, pageable);
    }

    public Card create(CardRequestDto request){

        if (request.getRows() * request.getCols() != request.getEvents().size()){
            throw new BadRequestException("Size of card (" + request.getRows() * request.getCols() + ") and number of events (" + request.getEvents().size() + ") doesn't match!");
        }

        Card card = new Card();
        card.setPrice(request.getPrice());
        card.setBingo_prize(request.getBingo_prize());
        card.setName(request.getName());
        card.setLine_prize(request.getLine_prize());
        card.setRows(request.getRows());
        card.setCols(request.getCols());
        card.setDate(LocalDateTime.now());

        card.setApproved(true);

        card.setEventsWithSignature(request.getEvents()
                .stream()
                .map(x ->
                        eventRepository.findById(x).
                                orElseThrow(() -> new BadRequestException("Event not found: " + x)))
                .toList());

        if (cardRepository.existsByEventsSignature(card.getEventsSignature())){
            throw new BadRequestException("Card already exists! Try to change events order or use another events!");
        }

        cardRepository.save(card);

        return card;
    }

    public Card update(CardRequestDto request, Long id){
        if (request.getRows() * request.getCols() != request.getEvents().size()){
            throw new BadRequestException("Size of card (" + request.getRows() * request.getCols() + ") and number of events (" + request.getEvents().size() + ") doesn't match!");
        }

        var card = get(id);

        card.setName(request.getName());
        card.setRows(request.getRows());
        card.setCols(request.getCols());

        card.setEventsWithSignature(request.getEvents()
                .stream()
                .map(x ->
                        eventRepository.findById(x)
                                .orElseThrow(() -> new BadRequestException("Event not found: " + x)))
                .collect(Collectors.toList()));

        card.setLine_prize(request.getLine_prize());
        card.setPrice(request.getPrice());
        card.setBingo_prize(request.getBingo_prize());

        cardRepository.save(card);

        return card;
    }

    public Card patch(Long id, CardPatchDto request){

        var card = cardRepository.findById(id).orElseThrow(() -> new BadRequestException("Card not found: " + id));

        int rows = request.getRows() == null? card.getRows(): request.getRows(),
                cols = request.getCols() == null? card.getCols(): request.getCols();

        if (request.getEvents() != null && rows * cols != request.getEvents().size()){
            throw new BadRequestException("Size of card (" + rows * cols + ") and number of events (" + request.getEvents().size() + ") doesn't match!");
        }

        if (request.getName() != null)
            card.setName(request.getName());
        if (request.getApproved() != null)
            card.setApproved(request.getApproved());
        if (request.getRows() != null)
            card.setRows(request.getRows());
        if (request.getCols() != null)
            card.setCols(request.getCols());

        if (request.getEvents() != null)
            card.setEventsWithSignature(request.getEvents()
                    .stream()
                    .map(x ->
                            eventRepository.findById(x).
                                    orElseThrow(() -> new BadRequestException("Event not found: " + x)))
                    .collect(Collectors.toList()));

        if (request.getLine_prize() != null)
            card.setLine_prize(request.getLine_prize());
        if (request.getPrice() != null)
            card.setPrice(request.getPrice());
        if (request.getBingo_prize() != null)
            card.setBingo_prize(request.getBingo_prize());

        cardRepository.save(card);

        return card;
    }

    public void buy(Long id, String token){
        var card = get(id);

        var user = userService.get(jwtService.extractUserId(token));

        logBufferService.addLog(LogLevel.INFO, "Buy card " + id + " requested by user " + user.getUsername());

        if (user.getBalance() - card.getPrice() < 0){
            throw new BadRequestException("Insufficient balance to buy this card!");
        }

        user.setBalance(user.getBalance() - card.getPrice());

        user.addCard(card);
        card.addUser(user);

        try {
            userRepository.save(user);
            cardRepository.save(card);
        } catch (DataIntegrityViolationException ex){
            throw new BadRequestException("Can't buy this card! It's already yours!");
        }
        finally {
            Transaction transaction = new Transaction();

            transaction.setUser(user);
            transaction.setAmount(card.getPrice());
            transaction.setType(TransactionType.CARD);
            transaction.setDate(new Date());
            transactionRepository.save(transaction);
        }
    }

    public void delete(Long id){
        var card = get(id);
        cardRepository.delete(card);
    }

    private synchronized void updateGeneration(Boolean running, Double progress){
        this.running = running;
        this.progress = progress;
    }

    @Async
    @Transactional
    public void generateCards(CardBuilderDto request){

        var events = eventRepository.findEventsByStatus(EventStatus.Pending);

        if (events.size() < request.getCols() * request.getRows())
            throw new BadRequestException("Size(" + request.getCols() * request.getRows() + ") is bigger than pending event count(" + events.size() + ")!");

        updateGeneration(true, 0.0);

        var shuffled = new ArrayList<>(events);

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
            card.setDate(LocalDateTime.now());

            do {
                Collections.shuffle(shuffled);
                card.setEventsWithSignature(shuffled.stream().limit((long) request.getCols() * request.getRows()).collect(Collectors.toList()));
                tries++;
                if (tries > 5) break;
            } while (cardRepository.existsByEventsSignature(card.getEventsSignature()) ||
                    cards.stream().anyMatch(c -> c.getEventsSignature().equals(card.getEventsSignature())));

            try {
                logBufferService.addLog(LogLevel.INFO, "[FOR TESTING] Waiting 10 seconds...");
                Thread.sleep(10000);
                logBufferService.addLog(LogLevel.INFO, "Waiting done!");
            } catch (InterruptedException e) {
                logBufferService.addLog(LogLevel.WARNING, "Error while waiting!");
            }

            if (tries > 5) break;

            cards.add(card);
            updateGeneration(true, ((double)cards.size())/request.getCount());

            logBufferService.addLog(LogLevel.INFO, "Card created with signature: " + card.getEventsSignature());
        }

        if (!cards.isEmpty())
            cardRepository.saveAll(cards);

        updateGeneration(false, null);
    }
}
