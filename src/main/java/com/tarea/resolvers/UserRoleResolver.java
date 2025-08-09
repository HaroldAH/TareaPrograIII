package com.tarea.resolvers;

import com.tarea.dtos.UserRoleDTO;
import com.tarea.resolvers.inputs.UserRoleInput;
import com.tarea.services.UserRoleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserRoleResolver {

    private final UserRoleService userRoleService;

    public UserRoleResolver(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    public List<UserRoleDTO> getAllUserRoles() {
        return userRoleService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    public UserRoleDTO getUserRoleById(@Argument Long userId, @Argument Long roleId) {
        return userRoleService.getById(userId, roleId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public UserRoleDTO createUserRole(@Argument UserRoleInput input) {
        return userRoleService.save(toDTO(input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Boolean deleteUserRole(@Argument Long userId, @Argument Long roleId) {
        userRoleService.delete(userId, roleId);
        return true;
    }

    private UserRoleDTO toDTO(UserRoleInput input) {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(input.getUserId());
        dto.setRoleId(input.getRoleId());
        return dto;
    }
}
