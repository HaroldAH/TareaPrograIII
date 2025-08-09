package com.tarea.resolvers;

import com.tarea.dtos.RoleDTO;
import com.tarea.resolvers.inputs.RoleInput;
import com.tarea.services.RoleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoleResolver {

    private final RoleService roleService;

    public RoleResolver(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    public List<RoleDTO> getAllRoles() {
        return roleService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    public RoleDTO getRoleById(@Argument Long id) {
        return roleService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public RoleDTO createRole(@Argument RoleInput input) {
        return roleService.save(toDTO(input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Boolean deleteRole(@Argument Long id) {
        roleService.delete(id);
        return true;
    }

    private RoleDTO toDTO(RoleInput input) {
        RoleDTO dto = new RoleDTO();
        dto.setId(input.getId());
        dto.setName(input.getName());
        dto.setPermissions(input.getPermissions());
        return dto;
    }
}
