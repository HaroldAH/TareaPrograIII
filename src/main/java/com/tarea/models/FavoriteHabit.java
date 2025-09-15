package com.tarea.models;

import jakarta.persistence.*;

@Entity
@Table(name = "favoriteHabit", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "habit_id"})
})
public class FavoriteHabit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habitactivity habit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Habitactivity getHabit() { return habit; }
    public void setHabit(Habitactivity habit) { this.habit = habit; }
}
