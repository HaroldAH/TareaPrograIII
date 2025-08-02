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
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id", nullable = false)
    private java.lang.Long id;

    @Column(name = "name", length = 20)
    private java.lang.String name;

    @Column(name = "permissions")
    private java.lang.String permissions;

}