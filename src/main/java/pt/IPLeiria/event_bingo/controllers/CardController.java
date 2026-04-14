package pt.IPLeiria.event_bingo.controllers;

import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.CardDto;
import pt.IPLeiria.event_bingo.dtos.CardRequestDto;
import pt.IPLeiria.event_bingo.dtos.UserDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.mapper.CardMapper;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CardController(CardMapper cardMapper, CardRepository cardRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.cardMapper = cardMapper;
        this.cardRepository = cardRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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

        card.setEventsSignature(request.getEvents().stream().map(String::valueOf).collect(Collectors.joining("-")));

        if (cardRepository.existsByEventsSignature(card.getEventsSignature())){
            throw new BadRequestException("Card already exists! Try to change events order or use another events!");
        }

        card.setEvents(
                request.getEvents().stream()
                        .map(id -> eventRepository.findById(id)
                                .orElseThrow(() -> new BadRequestException("Event not found: " + id)))
                        .toList()
        );

        cardRepository.save(card);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.created(uriBuilder.path("/cards/{id}").buildAndExpand(cardDto.getId()).toUri()).body(cardDto);
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<UserDto> buyCard(@PathVariable Long id, @RequestHeader("Authorization") Long user_id){
        var card = cardRepository.findById(id).orElseThrow(() -> new BadRequestException("Card not found: " + id));

        var user = userRepository.findById(user_id).orElseThrow(() -> new BadRequestException("User not found: " + user_id));

        user.addCard(card);
        card.addUser(user);

        userRepository.save(user);
        cardRepository.save(card);

        return ResponseEntity.ok().build();
    }
}
