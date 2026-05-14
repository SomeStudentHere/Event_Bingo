package pt.IPLeiria.event_bingo.dtos.transactions;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;

@AllArgsConstructor
@Getter
@Setter
public class MoneyDto {
    @NotBlank
    @Enumerated
    private TransactionType type;

    @Min(10)
    private double amount;

    @NotBlank
    @Pattern(regexp = "^\\d{4} ?\\d{4} ?\\d{4} ?\\d{4}$", message = "Invalid card number!")
    private String cardNumber;

    @NotBlank
    @Pattern(regexp = "^\\d{2}/\\d{2}$", message = "Invalid card valid field!")
    private String cardValid;

    @NotBlank
    private String cardHolderName;

    @NotBlank
    @Column(length = 3)
    private String ccNumber;
}
