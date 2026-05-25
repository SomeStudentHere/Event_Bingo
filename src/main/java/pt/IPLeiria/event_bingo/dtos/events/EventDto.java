package pt.IPLeiria.event_bingo.dtos.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class EventDto {
    private long id;
    private String prediction;
    private LocalDateTime date;
    private String sport;
    private EventStatus status;
    private String home_team;
    private String away_team;
}
