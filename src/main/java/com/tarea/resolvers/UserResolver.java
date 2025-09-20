package com.tarea.resolvers;

import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPermissionDTO;
import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import com.tarea.resolvers.inputs.ModulePermissionInput;
import com.tarea.resolvers.inputs.UserInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {

    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    /* ===================== QUERIES ===================== */

    @QueryMapping
    public List<UserDTO> getAllUsers() {
        SecurityUtils.requireView(Module.USERS);
        return userService.getAll();
    }

    @QueryMapping
    public UserDTO getUserById(@Argument Long id) {
        SecurityUtils.requireView(Module.USERS);
        return userService.getById(id);
    }

    /* ===================== MUTATIONS ===================== */

    @MutationMapping
    public UserDTO createUser(@Argument("input") UserInput input) {
        // El service maneja bootstrap (si count==0 no exige token; si no, exige USERS:MUTATE)
        List<UserPermissionDTO> perms = mapInputs(input.getPermissions());
        return userService.createUser(
                input.getName(),
                input.getEmail(),
                input.getPassword(),
                input.getIsAuditor(),
                perms
        );
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        SecurityUtils.requireMutate(Module.USERS);
        userService.delete(id);
        return true;
    }

    // ----- Permisos por módulo -----

    @MutationMapping
    public UserDTO setUserModulePermission(@Argument Long userId,
                                           @Argument Module module,
                                           @Argument ModulePermission permission) {
        SecurityUtils.requireMutate(Module.USERS);
        return userService.upsertPermission(userId, module, permission);
    }

    @MutationMapping
    public UserDTO revokeUserModulePermission(@Argument Long userId,
                                              @Argument Module module) {
        SecurityUtils.requireMutate(Module.USERS);
        return userService.revokePermission(userId, module);
    }

    @MutationMapping
    public UserDTO setUserPermissions(@Argument Long userId,
                                      @Argument List<ModulePermissionInput> permissions) {
        SecurityUtils.requireMutate(Module.USERS);
        if (permissions != null) {
            for (ModulePermissionInput p : permissions) {
                userService.upsertPermission(userId, getModule(p), getPermission(p));
            }
        }
        return userService.getById(userId);
    }

    @MutationMapping
    public UserDTO setAuditor(@Argument Long userId, @Argument Boolean auditor) {
        SecurityUtils.requireMutate(Module.USERS);
        return userService.setAuditor(userId, auditor != null && auditor);
    }

    /* ===================== HELPERS ===================== */

    private List<UserPermissionDTO> mapInputs(List<ModulePermissionInput> inputs) {
        if (inputs == null || inputs.isEmpty()) return List.of();
        return inputs.stream().map(p -> {
            UserPermissionDTO dto = new UserPermissionDTO();
            dto.setModule(getModule(p));
            dto.setPermission(getPermission(p));
            return dto;
        }).toList();
    }

    // Soporta tanto POJO (getters) como record (accessors)
    private Module getModule(ModulePermissionInput p) {
        try { return (Module) p.getClass().getMethod("getModule").invoke(p); }
        catch (Exception ignore) { /* fall through */ }
        try { return (Module) p.getClass().getMethod("module").invoke(p); }
        catch (Exception e) { throw new IllegalArgumentException("ModulePermissionInput.module no accesible"); }
    }

    private ModulePermission getPermission(ModulePermissionInput p) {
        try { return (ModulePermission) p.getClass().getMethod("getPermission").invoke(p); }
        catch (Exception ignore) { /* fall through */ }
        try { return (ModulePermission) p.getClass().getMethod("permission").invoke(p); }
        catch (Exception e) { throw new IllegalArgumentException("ModulePermissionInput.permission no accesible"); }
    }
}
