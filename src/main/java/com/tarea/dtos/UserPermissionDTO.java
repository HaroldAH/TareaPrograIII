package com.tarea.dtos;

import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import lombok.Data;

@Data
public class UserPermissionDTO {
    private Module module;
    private ModulePermission permission;
}
