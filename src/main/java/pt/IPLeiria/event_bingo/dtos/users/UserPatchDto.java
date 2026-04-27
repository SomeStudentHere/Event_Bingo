package pt.IPLeiria.event_bingo.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;

@Data
public class UserPatchDto {
    private String full_name;
    private String username;
    @Email(message = "Email must be valid")
    private String email;
    @Size(min = 3, message = "Password must be at least 3 characters")
    private String password;
    private Double balance;
    private UserStatus status;
}
