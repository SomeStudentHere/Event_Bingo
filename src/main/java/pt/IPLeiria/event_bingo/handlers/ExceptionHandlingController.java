package pt.IPLeiria.event_bingo.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlingController {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handle(BadRequestException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
