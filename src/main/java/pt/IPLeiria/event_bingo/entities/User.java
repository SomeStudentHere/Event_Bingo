package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.*;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String full_name;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private double balance;
    @Column(nullable = false)
    @Enumerated()
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;


    @Column
    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @ManyToMany(mappedBy = "users")
    private List<Card> cards;

    public void addCard(Card card) {
        cards.add(card);
    }
}
