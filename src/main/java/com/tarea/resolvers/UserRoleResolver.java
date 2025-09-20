package com.tarea.resolvers;

import com.tarea.dtos.UserRoleDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.UserRoleInput;
import com.tarea.services.UserRoleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class UserRoleResolver {

    private final UserRoleService userRoleService;

    public UserRoleResolver(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /* ===== QUERIES (CONSULT) ===== */

    @QueryMapping
    public List<UserRoleDTO> getAllUserRoles() {
        requireView(Module.USERS);
        return userRoleService.getAll();
    }

    @QueryMapping
    public UserRoleDTO getUserRoleById(@Argument Long userId, @Argument Long roleId) {
        requireView(Module.USERS);
        return userRoleService.getById(userId, roleId);
    }

    /* ===== MUTATIONS (MUTATE) ===== */

    @MutationMapping
    public UserRoleDTO createUserRole(@Argument UserRoleInput input) {
        requireMutate(Module.USERS);
        return userRoleService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteUserRole(@Argument Long userId, @Argument Long roleId) {
        requireMutate(Module.USERS);
        userRoleService.delete(userId, roleId);
        return true;
    }

    /* ===== Mapper ===== */

    private UserRoleDTO toDTO(UserRoleInput input) {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(input.getUserId());
        dto.setRoleId(input.getRoleId());
        return dto;
    }
}
