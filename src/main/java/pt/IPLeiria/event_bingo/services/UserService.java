package pt.IPLeiria.event_bingo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.auth.LoginRequestDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public List<User> list() {
        return userRepository.findAllByStatus(UserStatus.ACTIVE);
    }

    public List<User> listAll() {
        return userRepository.findAllByStatusIn(List.of(UserStatus.ACTIVE, UserStatus.SUSPENDED));
    }

    public User get(Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        if (user.getStatus().equals(UserStatus.SUSPENDED)) {
            throw new BadRequestException("User is Suspended");
        }

        if (user.getStatus().equals(UserStatus.DELETED)) {
            throw new NotFoundException("User not Found");
        }

        return user;
    }

    public User update(UserRegisterDto request, Long id){
        var user = get(id);

        if (!Objects.equals(request.getEmail(), user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User's email already exists!");
        }

        if (!Objects.equals(request.getUsername(), user.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("User's username already exists!");
        }

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFull_name(request.getFull_name());

        userRepository.save(user);

        return user;
    }

    public User patch(UserPatchDto request, Long id, User loggedUser) {
        var user = get(id);

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User's email already exists!");
        }

        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("User's username already exists!");
        }

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getFull_name() != null)
            user.setFull_name(request.getFull_name());
        if (request.getStatus() != null)
            user.setStatus(request.getStatus());
        if (request.getAvatar() != null){
            user.setAvatar(request.getAvatar());
        }

        // ADMIN ONLY
        if (loggedUser.getRole() == UserRole.ADMIN) {

            if (request.getBalance() != null)
                user.setBalance(request.getBalance());

            if (request.getUserRole() != null)
                user.setRole(request.getUserRole());
        }

        userRepository.save(user);

        return user;
    }

    public void delete(Long id){
        var user = get(id);

        user.setStatus(UserStatus.DELETED);

        userRepository.save(user);
    }

    public String login(LoginRequestDto request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return jwtService.generateToken(user.getId());
    }


    public String register(UserRegisterDto request){

        if (userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("User's email already exists!");
        }
        if (userRepository.existsByUsername(request.getUsername())){
            throw new BadRequestException("User's username already exists!");
        }

        User user = userMapper.toEntity(request);
        user.setBalance(0f);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        userRepository.save(user);

        return jwtService.generateToken(user.getId());
    }

    public User findByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validateUser(user);

        return user;
    }

    private void validateUser(User user){
        if (user.getStatus().equals(UserStatus.SUSPENDED)) {
            throw new BadRequestException("User is Suspended");
        }

        if (user.getStatus().equals(UserStatus.DELETED)) {
            throw new NotFoundException("User not Found");
        }
    }
}
