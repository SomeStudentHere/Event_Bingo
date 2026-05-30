package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.LogLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private LogLevel level;
    private String message;
    private LocalDateTime timestamp;
}
