package pt.IPLeiria.event_bingo.dtos.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class TransactionNoUserDto {
    private long id;
    private TransactionType type;
    private double amount;
    private Date date;
}
