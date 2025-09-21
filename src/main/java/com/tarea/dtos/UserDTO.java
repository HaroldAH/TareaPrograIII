package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;


    // usa wrapper para tolerar null en mapeos
    private Boolean isAuditor;

    // permisos por módulo que muestres en la UI/queries
    private List<UserPermissionDTO> permissions;
}
