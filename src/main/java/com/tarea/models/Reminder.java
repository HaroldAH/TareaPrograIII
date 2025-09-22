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
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   
    @JoinColumn(name = "user_id", nullable = false)        
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   
    @JoinColumn(name = "habit_id", nullable = false)
    private Habitactivity habit;

    @Column(name = "time", length = 10)                    
    private String time;

    @Column(name = "frequency", length = 20)               
    private String frequency;
}
