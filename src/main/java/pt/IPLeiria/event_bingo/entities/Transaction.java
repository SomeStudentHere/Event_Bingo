package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated()
    private TransactionType type;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Date date;

    public Transaction(User user, TransactionType type, double amount, Date date) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

}
