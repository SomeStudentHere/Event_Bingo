package pt.IPLeiria.event_bingo.dtos.cards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.dtos.events.EventDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CardDto {
    private long id;
    private String name;
    private int rows;
    private int cols;
    private double line_prize;
    private double bingo_prize;
    private double price;
    private boolean approved;
    private List<EventDto> events;
    private boolean terminated;
    private LocalDateTime date;
}
