package pt.IPLeiria.event_bingo.mapper;

import org.mapstruct.Mapper;
import pt.IPLeiria.event_bingo.dtos.CardDto;
import pt.IPLeiria.event_bingo.entities.Card;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardDto toDto(Card card);
    Card toEntity(CardDto cardDto);
}
