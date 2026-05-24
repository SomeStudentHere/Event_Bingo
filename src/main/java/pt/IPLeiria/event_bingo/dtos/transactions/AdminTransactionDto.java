package pt.IPLeiria.event_bingo.dtos.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminTransactionDto {
    private UserAllDto user;
    private List<TransactionNoUserDto> transactions;
}
