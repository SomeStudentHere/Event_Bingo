package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.cards.CardBuilderDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardPatchDto;
import pt.IPLeiria.event_bingo.dtos.cards.CardRequestDto;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.mapper.CardMapper;
import pt.IPLeiria.event_bingo.services.CardService;

import java.util.*;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardMapper cardMapper;
    private final CardService cardService;

    public CardController(CardMapper cardMapper, CardService cardService) {
        this.cardMapper = cardMapper;
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<CardDto>> getCards(Authentication authentication) {

        User user = null;

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User u) {
            user = u;
        }

        var cards = cardService.list(user);

        return ResponseEntity.ok(
                cards.stream()
                        .map(cardMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardMapper.toDto(cardService.get(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody CardRequestDto request, UriComponentsBuilder uriBuilder){

        Card card = cardService.create(request);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.created(uriBuilder.path("/cards/{id}").buildAndExpand(cardDto.getId()).toUri()).body(cardDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id, @Valid @RequestBody CardRequestDto request){

        Card card = cardService.update(request, id);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok(cardDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CardDto> patchCard(@PathVariable Long id, @Valid @RequestBody CardPatchDto request){

        Card card = cardService.patch(id, request);

        var cardDto = cardMapper.toDto(card);

        return ResponseEntity.ok(cardDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyCard(@PathVariable Long id, @RequestHeader("Authorization") String token){
        cardService.buy(id, token);

        return ResponseEntity.ok(Map.of("message", "Card bought successfully!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id){
        cardService.delete(id);

        return ResponseEntity.ok(Map.of("message", "Card deleted successfully!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
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
