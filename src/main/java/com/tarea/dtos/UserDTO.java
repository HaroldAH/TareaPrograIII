package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;

    // opcional/legado: puedes eliminarlo si ya no lo usas en ningún lado
    private String role;

    // usa wrapper para tolerar null en mapeos
    private Boolean isAuditor;

    // permisos por módulo que muestres en la UI/queries
    private List<UserPermissionDTO> permissions;
}
