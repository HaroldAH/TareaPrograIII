package com.tarea.resolvers;

import com.tarea.dtos.UserRoleDTO;
import com.tarea.resolvers.inputs.UserRoleInput;
import com.tarea.services.UserRoleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserRoleResolver {

    private final UserRoleService userRoleService;

    public UserRoleResolver(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @QueryMapping
    public List<UserRoleDTO> getAllUserRoles() {
        return userRoleService.getAll();
    }

    @QueryMapping
    public UserRoleDTO getUserRoleById(@Argument Long userId, @Argument Long roleId) {
        return userRoleService.getById(userId, roleId);
    }

    @MutationMapping
    public UserRoleDTO createUserRole(@Argument UserRoleInput input) {
        return userRoleService.save(toDTO(input));
    }

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
