package pt.IPLeiria.event_bingo.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class EventDto {
    private long id;
    private String prediction;
    private Date date;
    private String sport;
    private EventStatus status;
    private String home_team;
    private String away_team;
}
