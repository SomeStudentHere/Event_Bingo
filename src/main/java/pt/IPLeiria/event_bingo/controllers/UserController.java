package pt.IPLeiria.event_bingo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegisterDto request, UriComponentsBuilder uriBuilder){

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

        var userDto = userMapper.toDto(user);

        return ResponseEntity.created(uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri()).body(userDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegisterDto request){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFull_name(request.getFull_name());

        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserAllDto> patchUser(@PathVariable Long id, @Valid @RequestBody UserPatchDto request){
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

        return ResponseEntity.ok(userMapper.toAllDto(user));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not Found"));

        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }
}
