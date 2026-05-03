package pt.IPLeiria.event_bingo.services;

import org.springframework.stereotype.Service;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
        userRepository.save(user);

        return user;
    }

    public User update(UserRegisterDto request, Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
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
            user.setPassword(request.getPassword());
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
}
