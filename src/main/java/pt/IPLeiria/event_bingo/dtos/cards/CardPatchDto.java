package pt.IPLeiria.event_bingo.dtos.cards;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CardPatchDto {
    private String name;

    @Min(1)
    private Integer rows;

    @Min(1)
    private Integer cols;

    @DecimalMin(value = "0.01", message = "Price line must be greater than 0")
    private Double line_prize;

    @DecimalMin(value = "0.01", message = "Prize bingo must be greater than 0")
    private Double bingo_prize;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    private List<Long> events;

    private Boolean approved;
}
