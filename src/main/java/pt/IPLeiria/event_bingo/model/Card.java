package pt.IPLeiria.event_bingo.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
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

    public Card() {
    }

    public Card(String name, String size, double line_prize, double bingo_prize, double price, boolean approved) {
        this.name = name;
        this.size = size;
        this.line_prize = line_prize;
        this.bingo_prize = bingo_prize;
        this.price = price;
        this.approved = approved;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getLine_prize() {
        return line_prize;
    }

    public void setLine_prize(double line_prize) {
        this.line_prize = line_prize;
    }

    public double getBingo_prize() {
        return bingo_prize;
    }

    public void setBingo_prize(double bingo_prize) {
        this.bingo_prize = bingo_prize;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
