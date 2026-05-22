package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.dtos.transactions.TransactionDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<TransactionDto> getTransactions(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return transactionService.list(user)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MoneyDto request, @RequestHeader("Authorization") String token){
        transactionService.create(request, token);

        return ResponseEntity.status(201).body(Map.of("message", (request.getType() == TransactionType.DEPOSIT? "Deposit": "Withdraw")+ " successful"));
    }
}
