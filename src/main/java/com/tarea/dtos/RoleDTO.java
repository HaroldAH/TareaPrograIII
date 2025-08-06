package com.tarea.dtos;

import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String permissions;
}
