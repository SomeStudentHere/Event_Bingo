package pt.IPLeiria.event_bingo.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_SaveAll_ReturnSavedUsers() {
        User user = new User();
        user.setUsername("User1");
        user.setPassword("password1");
        user.setFull_name("User1");

        User userSaved = userRepository.save(user);

        Assertions.assertNotNull(userSaved);
        Assertions.assertTrue(userSaved.getId() > 0);
        Assertions.assertEquals(userSaved.getUsername(), user.getUsername());
        Assertions.assertEquals(userSaved.getPassword(), user.getPassword());
    }
}
