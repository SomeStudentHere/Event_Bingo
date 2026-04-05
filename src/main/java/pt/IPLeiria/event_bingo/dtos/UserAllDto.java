package pt.IPLeiria.event_bingo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;

@AllArgsConstructor
@Getter
@Setter
public class UserAllDto {
    private Long id;
    private String full_name;
    private String email;
    private double balance;
    private UserStatus status;
}
