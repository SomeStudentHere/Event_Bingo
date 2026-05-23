package pt.IPLeiria.event_bingo.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.IPLeiria.event_bingo.dtos.auth.LoginRequestDto;
import pt.IPLeiria.event_bingo.dtos.auth.LoginResponseDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        String token = userService.login(request);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @SecurityRequirements()
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegisterDto request){
        var token = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDto(token));
    }
}
