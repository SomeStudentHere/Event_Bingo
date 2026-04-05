package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String size;
    @Column(nullable = false)
    private double line_prize;
    @Column(nullable = false)
    private double bingo_prize;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private boolean approved;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "card_events",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "card_users",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    public Card(String name, String size, double line_prize, double bingo_prize, double price, boolean approved) {
        this.name = name;
        this.size = size;
        this.line_prize = line_prize;
        this.bingo_prize = bingo_prize;
        this.price = price;
        this.approved = approved;
    }
}
