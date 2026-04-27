package pt.IPLeiria.event_bingo.dtos.cards;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CardBuilderDto {
    @NotNull
    @Min(1)
    private Integer count;
    private Integer rows, cols;
    private Double bingo_prize_min, bingo_prize_max;
    private Double line_prize_min, line_prize_max;
    private Double price_min, price_max;
}
