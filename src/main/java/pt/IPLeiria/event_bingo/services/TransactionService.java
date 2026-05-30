package pt.IPLeiria.event_bingo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.entities.Transaction;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;

import java.util.Date;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, JwtService jwtService, UserRepository userRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Page<Transaction> list(User user, Pageable pageable) {

        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (isAdmin) {
            return transactionRepository.findAll(pageable);
        }

        return transactionRepository.findAllByUser(user, pageable);
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

        var user = userService.get(jwtService.extractUserId(token));


        if (moneyDto.getType() == TransactionType.WITHDRAW && user.getBalance() < moneyDto.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        var transaction = new Transaction();
        transaction.setAmount(moneyDto.getAmount());
        transaction.setDate(date);
        transaction.setType(moneyDto.getType());
        transaction.setUser(user);

        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() + (moneyDto.getType() == TransactionType.DEPOSIT? moneyDto.getAmount(): -moneyDto.getAmount()));

        userRepository.save(user);
    }

/* //removido pq já tem uma rota especifica para ir buscar transações de cada user
    public List<AdminTransactionDto> adminList() {
        return userRepository.findAll()
                .stream()
                .map(u -> new AdminTransactionDto(userMapper.toAllDto(u),
                                            transactionRepository.findAll(u)
                                                                .stream()
                                                                .map(transactionMapper::toNoUserDto)
                                                                .toList())
                ).toList();
    }*/

    public void updateClaimed(long transactionId, boolean claimed, User currentUser) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isOwner = transaction.getUser().getId() == currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied");
        }

        transaction.setClaimed(claimed);
        transactionRepository.save(transaction);
    }
}
