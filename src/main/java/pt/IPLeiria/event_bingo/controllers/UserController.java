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
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.repositories.UserRepository;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserMapper userMapper, UserService userService, JwtService jwtService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<UserDto> getUsers(){
        return userService.list()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        var user = userService.get(id);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegisterDto request, UriComponentsBuilder uriBuilder){
        var user = userService.create(request);

        var userDto = userMapper.toDto(user);

        return ResponseEntity.created(uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri()).body(userDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegisterDto request){
        var user = userService.update(request, id);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserAllDto> patchUser(@PathVariable Long id, @Valid @RequestBody UserPatchDto request){
        var user =  userService.patch(request, id);

        return ResponseEntity.ok(userMapper.toAllDto(user));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserAllDto> getMe(@RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(jwt);

        User user = userService.findByUsername(username);

        return ResponseEntity.ok(userMapper.toAllDto(user));
    }
}
