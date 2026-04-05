package pt.IPLeiria.event_bingo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pt.IPLeiria.event_bingo.dtos.UserDto;
import pt.IPLeiria.event_bingo.dtos.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/users/")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public List<UserDto> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUsers(@PathVariable Long id){
        var user = userRepository.findById(id).orElse(null);

        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@RequestParam UserRegisterDto request, UriComponentsBuilder uriBuilder){

        User user = userMapper.toEntity(request);
        userRepository.save(user);

        var userDto = userMapper.toDto(user);

        return ResponseEntity.created(uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri()).body(userDto);
    }

}
