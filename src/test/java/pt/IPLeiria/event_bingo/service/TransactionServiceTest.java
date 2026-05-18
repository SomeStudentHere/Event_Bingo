package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.TransactionService;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TransactionService transactionService;

    private MoneyDto moneyDtoDeposit, moneyDtoWithdraw;
    private User user;

    @BeforeEach
    public void setUp() {
        moneyDtoDeposit = new MoneyDto(TransactionType.DEPOSIT, 10d,
                "0000 0000 0000 0000", "02/30",
                "Test", "123");

        moneyDtoWithdraw = new MoneyDto(TransactionType.WITHDRAW, 10d,
                "0000 0000 0000 0000", "02/30",
                "Test", "123");

        user = User.builder()
                .id(0)
                .full_name("test")
                .username("test")
                .email("a@mail.com")
                .password("test")
                .balance(100)
                .avatar(null)
                .cards(new ArrayList<>())
                .status(UserStatus.ACTIVE)
                .build();

    }

    @Test
    void createTransaction() {
        when(jwtService.extractUsername(anyString())).thenReturn("test");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        Assertions.assertDoesNotThrow(() -> transactionService.create(moneyDtoDeposit, "<token>"));
    }

    @Test
    void createTransactionFailInvalidCard() {
        moneyDtoDeposit.setCardNumber("9999 0000 0000 0000");
        Assertions.assertThrows(BadRequestException.class,
                () -> transactionService.create(moneyDtoDeposit, "<token>"));

        moneyDtoWithdraw.setCardValid("01/01");
        Assertions.assertThrows(BadRequestException.class,
                () -> transactionService.create(moneyDtoWithdraw, "<token>"));
    }

    @Test
    void createTransactionFailInvalidType() {
        moneyDtoDeposit.setType(TransactionType.PRIZE);

        Assertions.assertThrows(BadRequestException.class,
                () -> transactionService.create(moneyDtoDeposit, "<token>"));
    }

    @Test
    void createTransactionFailInsufficientBalance() {
        user.setBalance(0);
        when(jwtService.extractUsername(anyString())).thenReturn("test");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(BadRequestException.class, () -> transactionService.create(moneyDtoWithdraw, "<token>"));
    }
}
