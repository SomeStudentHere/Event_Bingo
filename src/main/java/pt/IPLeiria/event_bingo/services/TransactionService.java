package pt.IPLeiria.event_bingo.services;

import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.entities.Transaction;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, JwtService jwtService, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public List<Transaction> list(User user) {

        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (isAdmin) {
            return transactionRepository.findAll();
        }

        return transactionRepository.findAllByUser(user);
    }

    public void create(MoneyDto moneyDto, String token) {
        if (moneyDto.getCardNumber().startsWith("9999")){
            throw new BadRequestException("Invalid card");
        }

        if (moneyDto.getType() != TransactionType.DEPOSIT && moneyDto.getType() != TransactionType.WITHDRAW) {
            throw new BadRequestException("Invalid type");
        }

        var date = new Date();

        var dateCard = new Date("01/" + moneyDto.getCardValid());
        if (dateCard.before(date)) {
            throw new BadRequestException("Invalid card");
        }

        var user = userRepository.findByUsername(jwtService.extractUsername(token)).orElseThrow(() -> new NotFoundException("User in token invalid"));


        var transaction = new Transaction();
        transaction.setAmount(moneyDto.getAmount());
        transaction.setDate(date);
        transaction.setType(moneyDto.getType());
        transaction.setUser(user);

        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() + (moneyDto.getType() == TransactionType.DEPOSIT? moneyDto.getAmount(): -moneyDto.getAmount()));

        userRepository.save(user);
    }
}
