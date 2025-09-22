package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;

    private Boolean isAuditor = false;     
    private Boolean isCoach = false;       

    private Long assignedCoachId;  

    private List<UserPermissionDTO> permissions;
}
