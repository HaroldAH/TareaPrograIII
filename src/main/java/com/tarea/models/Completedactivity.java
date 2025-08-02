package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "completedactivity")
public class Completedactivity {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_log_id")
    private Progresslog progressLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @Column(name = "completedAt", length = 10)
    private java.lang.String completedAt;

    @Column(name = "notes")
    private java.lang.String notes;

}