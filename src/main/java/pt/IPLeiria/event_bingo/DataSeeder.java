package pt.IPLeiria.event_bingo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.repositories.EventRepository;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {

            var password = passwordEncoder.encode("123");

            userRepository.save(User.builder()
                    .username("admin")
                    .password(password)
                    .full_name("admin")
                    .email("admin@mail.com")
                    .balance(0f)
                    .avatar(null)
                    .role(UserRole.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build());

            List<String> names = List.of("Maria", "Pedro", "Smith");
        }
    }
}
