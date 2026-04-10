package pt.IPLeiria.event_bingo.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CardRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @Min(1)
    private Integer rows;

    @Min(1)
    private Integer cols;

    @NotNull(message = "Price line is required")
    @DecimalMin(value = "0.01", message = "Price line must be greater than 0")
    private Double line_prize;

    @NotNull(message = "Prize bingo is required")
    @DecimalMin(value = "0.01", message = "Prize bingo must be greater than 0")
    private Double bingo_prize;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    private List<Long> events;
}
