package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "guide")
public class Guide {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @Column(name = "title", length = 100)
    private java.lang.String title;

    @Lob
    @Column(name = "content")
    private java.lang.String content;

    @Lob
    @Column(name = "category")
    private java.lang.String category;

}