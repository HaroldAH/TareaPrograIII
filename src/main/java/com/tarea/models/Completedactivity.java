package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "completedactivity")
public class Completedactivity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habitactivity habit;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "completedAt", length = 10)
    private String completedAt;

    @Column(name = "isCompleted")
    private Boolean isCompleted;

    @Column(name = "notes")
    private String notes;

}