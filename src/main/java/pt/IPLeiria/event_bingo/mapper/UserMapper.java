package pt.IPLeiria.event_bingo.mapper;

import org.mapstruct.Mapper;
import pt.IPLeiria.event_bingo.dtos.UserDto;
import pt.IPLeiria.event_bingo.dtos.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserRegisterDto request);
}
