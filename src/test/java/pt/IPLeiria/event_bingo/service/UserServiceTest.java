package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.IPLeiria.event_bingo.dtos.auth.LoginRequestDto;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.UserService;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;


    User user = User.builder()
            .id(0)
            .full_name("test")
            .username("test")
            .email("a@mail.com")
            .password("test")
            .balance(0)
            .avatar(null)
            .cards(new ArrayList<>())
            .status(UserStatus.ACTIVE)
            .build();

    UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getFull_name(), user.getAvatar(), user.getRole());
    UserAllDto userAllDto = new UserAllDto(user.getId(), user.getFull_name(), user.getUsername(), user.getEmail(), user.getBalance(), user.getStatus(), user.getAvatar(), new ArrayList<>(), user.getRole());
    UserPatchDto userPatchDto = new UserPatchDto();
    UserRegisterDto userRegisterDto = new UserRegisterDto(user.getFull_name(), user.getUsername(), user.getEmail(), user.getPassword(), user.getAvatar());
    LoginRequestDto loginRequestDto = new LoginRequestDto(user.getUsername(), user.getPassword());

    @Test
    public void testUserServiceUpdateSuccess() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        AtomicReference<User> reference = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() -> reference.set(userService.update(userRegisterDto, user.getId())));

        var savedUser = reference.get();

        Assertions.assertNotNull(savedUser);
    }

    @Test
    public void testUserServiceUpdateFailEmail() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class, () -> userService.update(userRegisterDto, user.getId()));
    }

    @Test
    public void testUserServiceUpdateFailUsername() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class, () -> userService.update(userRegisterDto, user.getId()));
    }

    @Test
    public void testUserServiceLogin() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getId())).thenReturn("token");

        AtomicReference<String> reference = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() -> reference.set(userService.login(loginRequestDto)));

        var token = reference.get();

        Assertions.assertNotNull(token);
    }

    @Test
    public void testUserServiceLoginFailUsername() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> userService.login(loginRequestDto));
    }

    @Test
    public void testUserServiceLoginFailPassword() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(false);

        Assertions.assertThrows(BadRequestException.class, () -> userService.login(loginRequestDto));
    }

    @Test
    public void testUserServiceRegister() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userMapper.toEntity(userRegisterDto)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user.getId())).thenReturn("token");


        AtomicReference<String> reference = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() -> reference.set(userService.register(userRegisterDto)));

        var token = reference.get();

        Assertions.assertNotNull(token);

        Assertions.assertNotNull(token);
    }

    @Test
    public void testUserServiceRegisterFailEmail() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class, () -> userService.register(userRegisterDto));
    }

    @Test
    public void testUserServiceRegisterFailUsername() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class, () -> userService.register(userRegisterDto));
    }
}
