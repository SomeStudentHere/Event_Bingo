package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.cards.CardBuilderDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardPatchDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardRequestDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.Event;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.CardMapper;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.services.CardGeneratorService;

import java.util.*;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CardGeneratorService cardGeneratorService;

    public CardController(CardMapper cardMapper, CardRepository cardRepository, EventRepository eventRepository, UserRepository userRepository, CardGeneratorService cardGeneratorService) {
        this.cardMapper = cardMapper;
        this.cardRepository = cardRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.cardGeneratorService = cardGeneratorService;
    }

    @GetMapping
    public List<CardDto> getCards(){
        return cardRepository.findAll()
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody CardRequestDto request, UriComponentsBuilder uriBuilder){

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

        card.setApproved(false);

        card.setEvents(request.getEvents(), eventRepository);

        if (cardRepository.existsByEventsSignature(card.getEventsSignature())){
            throw new BadRequestException("Card already exists! Try to change events order or use another events!");
        }

        cardRepository.save(card);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.created(uriBuilder.path("/cards/{id}").buildAndExpand(cardDto.getId()).toUri()).body(cardDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id, @Valid @RequestBody CardRequestDto request){
        if (request.getRows() * request.getCols() != request.getEvents().size()){
            throw new BadRequestException("Size of card (" + request.getRows() * request.getCols() + ") and number of events (" + request.getEvents().size() + ") doesn't match!");
        }

        var card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found: " + id));

        card.setName(request.getName());
        card.setRows(request.getRows());
        card.setCols(request.getCols());

        card.setEvents(request.getEvents(), eventRepository);

        card.setLine_prize(request.getLine_prize());
        card.setPrice(request.getPrice());
        card.setBingo_prize(request.getBingo_prize());

        cardRepository.save(card);

        return ResponseEntity.ok(cardMapper.toDto(card));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardDto> patchCard(@PathVariable Long id, @Valid @RequestBody CardPatchDto request){

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
            card.setEvents(request.getEvents(), eventRepository);

        if (request.getLine_prize() != null)
            card.setLine_prize(request.getLine_prize());
        if (request.getPrice() != null)
            card.setPrice(request.getPrice());
        if (request.getBingo_prize() != null)
            card.setBingo_prize(request.getBingo_prize());

        cardRepository.save(card);

        return ResponseEntity.ok(cardMapper.toDto(card));
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyCard(@PathVariable Long id, @RequestHeader("Authorization") Long user_id){
        var card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found: " + id));

        var user = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("User not found: " + user_id));

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

        return ResponseEntity.ok(Map.of("message", "Card bought successfully!"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id){
        var card = cardRepository.findById(id).orElse(null);

        if (card == null) return ResponseEntity.notFound().build();

        cardRepository.delete(card);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auto")
    public ResponseEntity<?> createCardsAuto(@Valid @RequestBody CardBuilderDto request){

        if (cardGeneratorService.isRunning()){
            return ResponseEntity
                    .status(409)
                    .body(Map.of("error",
                            String.format("Builder is still working! %.2f%%", cardGeneratorService.getProgress())));
        }

        var events = eventRepository.findEventsByStatus(EventStatus.Pending);

        if (events.size() < request.getCols() * request.getRows())
            throw new BadRequestException("Size(" + request.getCols() * request.getRows() + ") is bigger than pending event count(" + events.size() + ")!");

        cardGeneratorService.generateCards(request, events.stream().map(Event::getId).toList(), cardRepository, eventRepository);

        return ResponseEntity.accepted().body(Map.of("message", "Process started!"));
    }

    @GetMapping("/auto")
    public ResponseEntity<?> getCardsAuto(){

        var map = new Hashtable<String, String>();

        map.put("running", String.valueOf(cardGeneratorService.isRunning()));

        if (cardGeneratorService.isRunning())
            map.put("progress", String.valueOf(cardGeneratorService.getProgress()));

        return ResponseEntity
                .status(cardGeneratorService.isRunning()? 200: 404)
                .body(map);
    }
}
