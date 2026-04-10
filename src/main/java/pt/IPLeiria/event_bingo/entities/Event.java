package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String home_team;
    @Column
    private String away_team;
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
}
