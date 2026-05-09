package pt.IPLeiria.event_bingo.mapper;

import org.mapstruct.Mapper;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;

@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserRegisterDto request);
    UserAllDto toAllDto(User user);
}
