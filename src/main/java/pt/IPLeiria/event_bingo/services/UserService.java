package pt.IPLeiria.event_bingo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.auth.LoginRequestDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;

import java.util.List;

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
        return userRepository.findAll();
    }

    public User get(Long id){

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));
    }

    public User create(UserRegisterDto request){

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
        userRepository.save(user);

        return user;
    }

    public User update(UserRegisterDto request, Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("User's email already exists!");
        }

        if (userRepository.existsByUsername(request.getUsername()) && !user.getUsername().equals(request.getUsername())) {
            throw new BadRequestException("User's username already exists!");
        }

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFull_name(request.getFull_name());

        userRepository.save(user);

        return user;
    }

    public User patch(UserPatchDto request, Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

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
        if (request.getBalance() != null)
            user.setBalance(user.getBalance() + request.getBalance());

        userRepository.save(user);

        return user;
    }

    public void delete(Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        userRepository.delete(user);
    }

    public String login(LoginRequestDto request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return jwtService.generateToken(user.getUsername());
    }
}
