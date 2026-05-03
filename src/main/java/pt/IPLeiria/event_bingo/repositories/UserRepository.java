package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.IPLeiria.event_bingo.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
