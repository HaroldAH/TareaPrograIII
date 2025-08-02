package com.tarea.rutinas.saludables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "authtoken")
public class Authtoken {
    @Id
    @Column(name = "token", nullable = false)
    private java.lang.String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiresAt")
    private java.time.Instant expiresAt;

}