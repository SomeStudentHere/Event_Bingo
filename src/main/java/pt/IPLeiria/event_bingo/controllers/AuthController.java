package pt.IPLeiria.event_bingo.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.IPLeiria.event_bingo.dtos.auth.LoginRequestDto;
import pt.IPLeiria.event_bingo.dtos.auth.LoginResponseDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import pt.IPLeiria.event_bingo.services.UserService;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final LogBufferService logBufferService;
    private final ObjectMapper objectMapper;

    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        logBufferService.addLog(LogLevel.INFO, "Login request received for user " + request.getUsername());

        String token = userService.login(request);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @SecurityRequirements()
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegisterDto request){

        UserRegisterDto copy = new UserRegisterDto(request.getFull_name(),
                request.getUsername(),
                request.getEmail(),
                "<PASSWORD>",
                request.getAvatar());

        logBufferService.addLog(LogLevel.INFO, "Register request received with data: " + objectMapper.writeValueAsString(copy));

        var token = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDto(token));
    }
}
