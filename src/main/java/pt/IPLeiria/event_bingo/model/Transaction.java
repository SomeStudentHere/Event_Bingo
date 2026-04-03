package pt.IPLeiria.event_bingo.model;

import jakarta.persistence.*;
import pt.IPLeiria.event_bingo.model.enums.TransactionType;
import pt.IPLeiria.event_bingo.model.enums.UserStatus;

import java.util.Date;

@Entity
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

    public Transaction() {
    }

    public Transaction(User user, TransactionType type, double amount, Date date) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
