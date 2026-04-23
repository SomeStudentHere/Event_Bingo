package pt.IPLeiria.event_bingo.mapper;

import org.mapstruct.Mapper;
import pt.IPLeiria.event_bingo.dtos.events.EventDto;
import pt.IPLeiria.event_bingo.dtos.events.EventRequestDto;
import pt.IPLeiria.event_bingo.entities.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto toDto(Event event);
    Event toEntity(EventRequestDto eventDto);
}
