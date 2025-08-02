package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "routineactivity")
public class Routineactivity {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @Column(name = "duration")
    private java.lang.Integer duration;

    @Column(name = "targetTime", length = 10)
    private java.lang.String targetTime;

    @Column(name = "notes")
    private java.lang.String notes;

}