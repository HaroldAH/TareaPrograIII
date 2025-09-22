package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_auditor", nullable = false)
    private Boolean isAuditor = false;

     
    @Column(name = "is_coach", nullable = false)
    private Boolean isCoach = false;

     
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_coach_id")
    private User assignedCoach;

     
     
     
}
