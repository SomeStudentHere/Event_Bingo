package pt.IPLeiria.event_bingo.model;

import jakarta.persistence.*;
import pt.IPLeiria.event_bingo.model.enums.EventStatus;

import java.util.Date;
import java.util.List;

@Entity
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

    public Event() {
    }

    public Event(String prediction, Date date, String sport, EventStatus status) {
        this.prediction = prediction;
        this.date = date;
        this.sport = sport;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
