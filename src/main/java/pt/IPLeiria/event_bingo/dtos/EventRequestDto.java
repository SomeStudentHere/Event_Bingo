package pt.IPLeiria.event_bingo.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class EventRequestDto {
    @NotBlank(message = "Prediction is required")
    private String prediction;

    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @NotBlank(message = "Sport is required")
    private String sport;

    private String home_team;
    private String away_team;
}
