// com/tarea/models/Completedactivity.java
package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "completedactivity")
public class Completedactivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ← importante para evitar INTERNAL_ERROR por id manual
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  // ← dueño obligatorio
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "habit_id")
    private Habitactivity habit;

    @Column(name = "date") // DATE en BD
    private LocalDate date;

    @Column(name = "completedAt", length = 10) // "HH:mm"
    private String completedAt;

    @Column(name = "notes", length = 255)
    private String notes;
}
