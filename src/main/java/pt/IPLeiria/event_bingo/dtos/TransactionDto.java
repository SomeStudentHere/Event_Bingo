package pt.IPLeiria.event_bingo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class TransactionDto {
    private long id;
    private UserAllDto user;
    private TransactionType type;
    private double amount;
    private Date date;
}
