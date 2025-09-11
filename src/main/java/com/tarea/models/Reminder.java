package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reminder")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // <-- Necesario para AUTO_INCREMENT
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  // <-- Negocio: siempre debe tener dueño
    @JoinColumn(name = "user_id", nullable = false)       //     (tu Service ya lo valida)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  // <-- Igual para el hábito
    @JoinColumn(name = "habit_id", nullable = false)
    private Habitactivity habit;

    @Column(name = "time", length = 10)                   // Ej: "07:30"
    private String time;

    @Column(name = "frequency", length = 20)              // Ej: "DAILY" o "MON,WED,FRI"
    private String frequency;
}
