package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.dtos.transactions.TransactionDto;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.mapper.TransactionMapper;
import pt.IPLeiria.event_bingo.services.TransactionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;

    public TransactionController(TransactionMapper transactionMapper, TransactionService transactionService) {
        this.transactionMapper = transactionMapper;
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionDto> getTrasactions(@RequestHeader("Authorization") String token){
        return transactionService.list(token)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MoneyDto request, @RequestHeader("Authorization") String token){
        transactionService.create(request, token);

        return ResponseEntity.status(201).body(Map.of("message", (request.getType() == TransactionType.DEPOSIT? "Deposit": "Withdraw")+ " successful"));
    }
}
