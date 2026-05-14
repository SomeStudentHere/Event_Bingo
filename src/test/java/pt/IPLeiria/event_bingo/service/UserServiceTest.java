package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.services.UserService;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        User user = User.builder()
                .username("test")
                .full_name("test")
                .email("a@mail.com")
                .password("test")
                .status(UserStatus.ACTIVE)
                .balance(0f)
                .cards(new ArrayList<>())
                .build();

        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername(user.getUsername());
        userRegisterDto.setFull_name(user.getFull_name());
        userRegisterDto.setEmail(user.getEmail());
        userRegisterDto.setPassword(user.getPassword());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(userMapper.toEntity(Mockito.any(UserRegisterDto.class))).thenReturn(user);

        var savedUser = userService.register(userRegisterDto);

        Assertions.assertNotNull(savedUser);
        //Assertions.assertEquals(user.getUsername(), savedUser.getUsername());
    }
}
