package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String prediction;
    @Column(nullable = false)
    private Date date;
    @Column(nullable = false)
    private String sport;
    @Column(nullable = false)
    @Enumerated()
    private EventStatus status;

    @ManyToMany(mappedBy = "events")
    private List<Card> cards;

    public Event(String prediction, Date date, String sport, EventStatus status) {
        this.prediction = prediction;
        this.date = date;
        this.sport = sport;
        this.status = status;
    }

}
