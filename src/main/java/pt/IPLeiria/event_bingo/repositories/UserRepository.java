package pt.IPLeiria.event_bingo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.IPLeiria.event_bingo.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
