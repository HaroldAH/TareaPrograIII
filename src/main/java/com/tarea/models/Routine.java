package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "routine")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

     
    @Column(name = "daysOfWeek", length = 20)
    private String daysOfWeek;
}
