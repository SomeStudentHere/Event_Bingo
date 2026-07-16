package pt.IPLeiria.event_bingo.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String avatar;
    private UserRole role;
}
