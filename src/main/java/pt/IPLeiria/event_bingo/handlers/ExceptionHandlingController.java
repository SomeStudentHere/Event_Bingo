package pt.IPLeiria.event_bingo.handlers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.exeptions.InternalErrorException;
import pt.IPLeiria.event_bingo.exeptions.NotFoundException;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
public class ExceptionHandlingController {
    private final LogBufferService logBufferService;
    private final ObjectMapper objectMapper;

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<?> handle(InternalErrorException ex) {
        logBufferService.addLog(LogLevel.ERROR, ex.getMessage());
        return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handle(BadRequestException ex) {
        logBufferService.addLog(LogLevel.WARNING, ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handle(NotFoundException ex) {
        logBufferService.addLog(LogLevel.WARNING, ex.getMessage());
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        logBufferService.addLog(LogLevel.WARNING, objectMapper.writeValueAsString(errors));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidFormat(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {

            String fieldName = ife.getPath().get(0).getPropertyName();

            if (ife.getTargetType().equals(Boolean.class)) {

                logBufferService.addLog(LogLevel.WARNING, fieldName + " is not a boolean");

                return ResponseEntity.badRequest().body(
                        Map.of(fieldName, "must be true or false")
                );
            }
        }

        logBufferService.addLog(LogLevel.WARNING, "Invalid request format");

        return ResponseEntity.badRequest().body(
                Map.of("error", "Invalid request format")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        logBufferService.addLog(LogLevel.ERROR,  ex.getMessage());
        return ResponseEntity.internalServerError().body(Map.of("error", "Internal Server Error"));
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> authException(Exception ex) {
        logBufferService.addLog(LogLevel.WARNING,  ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
