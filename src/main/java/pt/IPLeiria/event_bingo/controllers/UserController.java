package pt.IPLeiria.event_bingo.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.exeptions.InternalErrorException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.UserService;

import java.io.IOException;
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
    public ResponseEntity<UserDto> getUser(HttpServletResponse response, @PathVariable Long id, @RequestHeader("Authorization") String token){
        var user = userService.get(id);

        if (!token.isEmpty()){
            String username = jwtService.extractUsername(token);
            if (username.equals(user.getUsername())){
                try {
                    response.sendRedirect("/users/me");
                } catch (IOException e) {
                    throw new InternalErrorException("Had a problem redirecting to the user page!");
                } finally {
                    return null;
                }
            }
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

/* replace pelo /auth/register
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegisterDto request, UriComponentsBuilder uriBuilder){
        var user = userService.create(request);

        var userDto = userMapper.toDto(user);

        return ResponseEntity.created(uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri()).body(userDto);
    }*/

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

        String username = jwtService.extractUsername(token);

        User user = userService.findByUsername(username);

        return ResponseEntity.ok(userMapper.toAllDto(user));
    }
}
