package com.tarea.resolvers.inputs;

import com.tarea.models.Module;
import com.tarea.models.ModulePermission;

public record ModulePermissionInput(Module module, ModulePermission permission) {}
