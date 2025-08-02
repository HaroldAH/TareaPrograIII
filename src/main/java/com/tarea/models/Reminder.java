package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reminder")
public class Reminder {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @Column(name = "time", length = 10)
    private java.lang.String time;

    @Column(name = "frequency", length = 20)
    private java.lang.String frequency;

}