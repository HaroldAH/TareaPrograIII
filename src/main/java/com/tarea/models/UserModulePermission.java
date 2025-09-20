package com.tarea.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_module_permission",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "module"}))
@Getter @Setter
@NoArgsConstructor
public class UserModulePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relación con user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // enums como columnas tipo enum o varchar (ya lo tenías mapeado)
    @Enumerated(EnumType.STRING)
    @Column(name = "module", nullable = false, length = 20)
    private Module module;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 10)
    private ModulePermission permission;

    // ⬅️ ESTE es el constructor que usas desde el service
    public UserModulePermission(User user, Module module, ModulePermission permission) {
        this.user = user;
        this.module = module;
        this.permission = permission;
    }
}
