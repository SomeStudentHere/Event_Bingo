package pt.IPLeiria.event_bingo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.IPLeiria.event_bingo.dtos.auth.LoginRequestDto;
import pt.IPLeiria.event_bingo.dtos.auth.LoginResponseDto;
import pt.IPLeiria.event_bingo.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        String token = userService.login(request);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
