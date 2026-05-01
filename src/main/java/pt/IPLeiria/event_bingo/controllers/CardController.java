package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.cards.CardBuilderDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardPatchDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardRequestDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.mapper.CardMapper;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.services.CardService;

import java.util.*;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final CardService cardService;

    public CardController(CardMapper cardMapper, CardRepository cardRepository, CardService cardService) {
        this.cardMapper = cardMapper;
        this.cardRepository = cardRepository;
        this.cardService = cardService;
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

        Card card = cardService.create(request);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.created(uriBuilder.path("/cards/{id}").buildAndExpand(cardDto.getId()).toUri()).body(cardDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id, @Valid @RequestBody CardRequestDto request){

        Card card = cardService.update(request, id);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok(cardDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardDto> patchCard(@PathVariable Long id, @Valid @RequestBody CardPatchDto request){

        Card card = cardService.patch(id, request);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyCard(@PathVariable Long id, @RequestHeader("Authorization") Long user_id){
        cardService.buy(id, user_id);

        return ResponseEntity.ok(Map.of("message", "Card bought successfully!"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id){
        cardService.delete(id);

        return ResponseEntity.ok(Map.of("message", "Card deleted successfully!"));
    }

    @PostMapping("/auto")
    public ResponseEntity<?> createCardsAuto(@Valid @RequestBody CardBuilderDto request){

        if (cardService.isRunning()){
            return ResponseEntity
                    .status(409)
                    .body(Map.of("error",
                            String.format("Builder is still working! %.2f%%", cardService.getProgress())));
        }


        cardService.generateCards(request);

        return ResponseEntity.accepted().body(Map.of("message", "Process started!"));
    }

    @GetMapping("/auto")
    public ResponseEntity<?> getCardsAuto(){

        var map = new Hashtable<String, String>();

        map.put("running", String.valueOf(cardService.isRunning()));

        if (cardService.isRunning())
            map.put("progress", String.valueOf(cardService.getProgress()));

        return ResponseEntity
                .status(cardService.isRunning()? 200: 404)
                .body(map);
    }
}
