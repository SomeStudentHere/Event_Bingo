package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int rows;
    @Column(nullable = false)
    private int cols;
    @Column(nullable = false)
    private double line_prize;
    @Column(nullable = false)
    private double bingo_prize;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private boolean approved;
    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "card_events",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;

    @Column(unique = true, nullable = false)
    private String eventsSignature;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "card_users",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "user_id"})
    )
    private List<User> users;

    private boolean terminated;

    public void addUser(User user) {
        users.add(user);
    }

    public void setEventsWithSignature(List<Event> events) {

        this.events = events;

        this.setEventsSignature(events.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.joining("-")));
    }
}
