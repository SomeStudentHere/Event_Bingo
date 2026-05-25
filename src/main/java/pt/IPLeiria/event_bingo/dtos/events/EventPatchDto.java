package pt.IPLeiria.event_bingo.dtos.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class EventPatchDto {
    private String prediction;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private String sport;

    private String home_team;
    private String away_team;

    private EventStatus status;
}
