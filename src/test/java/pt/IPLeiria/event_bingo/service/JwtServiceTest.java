package pt.IPLeiria.event_bingo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pt.IPLeiria.event_bingo.security.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                jwtService,
                "SECRET",
                "assigned-by-gerson-and-edgar-backend"
        );
    }

    @Test
    void testGenerateAndExtractUserId() {
        Long userId = 1L;

        String token = jwtService.generateToken(userId);
        Long extracted = jwtService.extractUserId(token);

        assertEquals(userId, extracted);
    }

    @Test
    void testExtractUserIdWithBearerPrefix() {
        Long userId = 1L;

        String token = "Bearer " + jwtService.generateToken(userId);
        Long extracted = jwtService.extractUserId(token);

        assertEquals(userId, extracted);
    }

    @Test
    void testInvalidToken() {
        assertThrows(Exception.class,
                () -> jwtService.extractUserId("invalid.token"));
    }
}