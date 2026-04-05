package pt.IPLeiria.event_bingo.dtos;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String full_name;
    private String email;
    private String password;
}
