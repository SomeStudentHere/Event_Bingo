package pt.IPLeiria.event_bingo.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
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
    public List<?> getUsers(Authentication authentication) {

        boolean isAdmin = false;

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {

            User user = (User) authentication.getPrincipal();
            isAdmin = user.getRole() == UserRole.ADMIN;
        }

        boolean finalIsAdmin = isAdmin;

        return userService.list()
                .stream()
                .map(user -> {
                    if (finalIsAdmin) {
                        return userMapper.toAllDto(user);
                    }
                    return userMapper.toDto(user);
                })
                .toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id, Authentication authentication) {

        User user = userService.get(id);

        boolean isAdmin = authentication != null
                && authentication.getPrincipal() instanceof User authUser
                && authUser.getRole() == UserRole.ADMIN;

        boolean isOwner = authentication != null
                && authentication.getPrincipal() instanceof User authUser
                && authUser.getUsername().equals(user.getUsername());

        if (isAdmin || isOwner) {
            return ResponseEntity.ok(userMapper.toAllDto(user));
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

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegisterDto request){
        var user = userService.update(request, id);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PatchMapping("{id}")
    public ResponseEntity<UserAllDto> patchUser(Authentication authentication, @PathVariable Long id, @Valid @RequestBody UserPatchDto request){

        User loggedUser = (User) authentication.getPrincipal();

        var user = userService.patch(request, id, loggedUser);

        return ResponseEntity.ok(userMapper.toAllDto(user));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserAllDto> getMe(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(userMapper.toAllDto(user));
    }
}
