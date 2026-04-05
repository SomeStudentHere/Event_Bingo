package pt.IPLeiria.event_bingo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CardDto {
    private long id;
    private String name;
    private String size;
    private double line_prize;
    private double bingo_prize;
    private double price;
    private boolean approved;
    private List<EventDto> events;
}
