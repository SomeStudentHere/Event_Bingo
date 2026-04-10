package pt.IPLeiria.event_bingo.mapper;

import org.mapstruct.Mapper;
import pt.IPLeiria.event_bingo.dtos.TransactionDto;
import pt.IPLeiria.event_bingo.entities.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);
}
