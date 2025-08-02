package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "progresslog")
public class Progresslog {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Column(name = "date")
    private java.time.LocalDate date;

    @Lob
    @Column(name = "status")
    private java.lang.String status;

}