package com.tarea.rutinas.saludables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "habit")
public class Habit {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @Column(name = "name", length = 20)
    private java.lang.String name;

    @Column(name = "category", length = 50)
    private java.lang.String category;

    @Column(name = "description", length = 100)
    private java.lang.String description;

}