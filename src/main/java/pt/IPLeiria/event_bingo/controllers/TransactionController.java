package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.mapper.TransactionMapper;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import pt.IPLeiria.event_bingo.services.TransactionService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;
    private final LogBufferService logBufferService;
    private final ObjectMapper objectMapper;

    public TransactionController(TransactionMapper transactionMapper, TransactionService transactionService, LogBufferService logBufferService, ObjectMapper objectMapper) {
        this.transactionMapper = transactionMapper;
        this.transactionService = transactionService;
        this.logBufferService = logBufferService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<?> getTransactions(Authentication authentication) {

        logBufferService.addLog(LogLevel.INFO, "List transactions");

        User user = (User) authentication.getPrincipal();

        if (user == null){
            throw new BadRequestException("User is not logged in!");
        }

        if (user.getRole() == UserRole.ADMIN) {
            return transactionService.adminList();
        }
        else {
            return transactionService.list(user)
                    .stream()
                    .map(transactionMapper::toDto)
                    .toList();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MoneyDto request, @RequestHeader("Authorization") String token){

        MoneyDto copy = new MoneyDto(request.getType(), request.getAmount(), "<CardNumber>", "<CardValid>", "<CardHolderName>", "<CcNumber>");
        logBufferService.addLog(LogLevel.INFO, "Create transactions with data: " + objectMapper.writeValueAsString(copy));

        transactionService.create(request, token);

        return ResponseEntity.status(201).body(Map.of("message", (request.getType() == TransactionType.DEPOSIT? "Deposit": "Withdraw")+ " successful"));
    }
}
