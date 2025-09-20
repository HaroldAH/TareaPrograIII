package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    // lo puedes dejar por legado si quieres
    @Column(name = "role", length = 50)
    private String role;

    // ⬇️ NUEVO
    @Column(name = "is_auditor", nullable = false)
    private Boolean isAuditor = false;
}
