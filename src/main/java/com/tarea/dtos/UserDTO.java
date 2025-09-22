package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;

    private Boolean isAuditor = false;    // ya lo tenías
    private Boolean isCoach = false;      // NUEVO

    private Long assignedCoachId; // NUEVO (para la UI)

    private List<UserPermissionDTO> permissions;
}
