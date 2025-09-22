package com.tarea.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "habitactivity")
public class Habitactivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "targetTime")
    private java.time.LocalTime targetTime;

    @Column(name = "notes")
    private String notes;
}
