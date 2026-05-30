package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

    Page<User> findAllByStatusIn(Collection<UserStatus> statuses, Pageable pageable);
}
