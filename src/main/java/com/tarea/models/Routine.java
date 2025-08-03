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
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @Column(name = "title", length = 100)
    private java.lang.String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "daysOfWeek", length = 20)
    private java.lang.String daysOfWeek;

}