package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.dtos.transactions.TransactionPatchDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.mapper.TransactionMapper;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import pt.IPLeiria.event_bingo.services.TransactionService;
import pt.IPLeiria.event_bingo.services.UserService;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;
    private final LogBufferService logBufferService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public TransactionController(TransactionMapper transactionMapper, TransactionService transactionService, LogBufferService logBufferService, ObjectMapper objectMapper, UserService userService) {
        this.transactionMapper = transactionMapper;
        this.transactionService = transactionService;
        this.logBufferService = logBufferService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getTransactions(Authentication authentication, @ParameterObject Pageable pageable) {

        logBufferService.addLog(LogLevel.INFO, "List transactions");

        User user = (User) authentication.getPrincipal();

        if (user == null){
            throw new BadRequestException("User is not logged in!");
        }

        /*if (user.getRole() == UserRole.ADMIN) { //removido pq já tem uma rota especifica para ir buscar transações de cada user
            return ResponseEntity.ok(transactionService.adminList());
        }
        else {*/
            return ResponseEntity.ok(transactionService.list(user, pageable).map(transactionMapper::toDto));
        //}
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MoneyDto request, @RequestHeader("Authorization") String token){

        MoneyDto copy = new MoneyDto(request.getType(), request.getAmount(), "<CardNumber>", "<CardValid>", "<CardHolderName>", "<CcNumber>");
        logBufferService.addLog(LogLevel.INFO, "Create transactions with data: " + objectMapper.writeValueAsString(copy));

        transactionService.create(request, token);

        return ResponseEntity.status(201).body(Map.of("message", (request.getType() == TransactionType.DEPOSIT? "Deposit": "Withdraw")+ " successful"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<?> getTransaction(@PathVariable long id, Pageable pageable){
        logBufferService.addLog(LogLevel.INFO, "Get transaction with id: " + id);

        var user = userService.get(id);

        return ResponseEntity.ok(
                transactionService.list(user, pageable).map(transactionMapper::toDto)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("{id}")
    public ResponseEntity<?> patchTransaction(
            @PathVariable long id,
            @Valid @RequestBody TransactionPatchDto request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        transactionService.updateClaimed(id, request.getClaimed(), user);

        return ResponseEntity.ok(Map.of("message", "Transaction updated successfully"));
    }

}
