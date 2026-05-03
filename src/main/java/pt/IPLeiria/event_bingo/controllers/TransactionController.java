package pt.IPLeiria.event_bingo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.IPLeiria.event_bingo.dtos.transactions.TransactionDto;
import pt.IPLeiria.event_bingo.mapper.TransactionMapper;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.services.TransactionService;

import java.util.List;

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
    public List<TransactionDto> getTrasactions(){
        return transactionService.list()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }
}
