package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;

    private Boolean isAuditor;    // ya lo tenías
    private Boolean isCoach;      // NUEVO

    private Long assignedCoachId; // NUEVO (para la UI)

    private List<UserPermissionDTO> permissions;
}
