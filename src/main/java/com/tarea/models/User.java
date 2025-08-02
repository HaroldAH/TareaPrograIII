package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @Column(name = "name", length = 20)
    private java.lang.String name;

    @Column(name = "email", length = 100)
    private java.lang.String email;

    @Column(name = "password")
    private java.lang.String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

}