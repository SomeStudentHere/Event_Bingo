package pt.IPLeiria.event_bingo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import pt.IPLeiria.event_bingo.entities.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
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
    private LocalDateTime date;
    @Column(nullable = false)
    private String sport;
    @Column(nullable = false)
    @Enumerated()
    private EventStatus status;

    @ManyToMany(mappedBy = "events")
    private List<Card> cards;
}
