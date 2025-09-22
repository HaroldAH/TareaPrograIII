package com.tarea.resolvers;

import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPageDTO;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import com.tarea.dtos.UserPageDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.PageRequest;



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

    /** NUEVO: lista SOLO los usuarios asignados al coach autenticado */
    @QueryMapping
    public List<UserDTO> myCoachees() {
        Long me = SecurityUtils.userId();
        if (me == null) throw new AccessDeniedException("Unauthorized");

        UserDTO meDto = userService.getById(me);
        if (!Boolean.TRUE.equals(meDto.getIsCoach())) {
            throw new AccessDeniedException("Forbidden");
        }
        return userService.getCoachees(me);
    }

    /** Ajustado: si el caller es coach y pide su propia lista, lo permitimos; si no, requiere permiso USERS */
    @QueryMapping
    public List<UserDTO> usersAssignedToCoach(@Argument Long coachId) {
        Long me = null;
        try { me = SecurityUtils.userId(); } catch (Exception ignore) {}

        if (me != null) {
            UserDTO meDto = userService.getById(me);
            if (Boolean.TRUE.equals(meDto.getIsCoach()) && coachId.equals(me)) {
                return userService.getCoachees(coachId);
            }
        }

        SecurityUtils.requireView(Module.USERS);
        return userService.getCoachees(coachId);
    }

    /* ===================== MUTATIONS ===================== */

    @MutationMapping
    public UserDTO createUser(@Argument("input") UserInput input) {
        List<UserPermissionDTO> perms = mapInputs(input.getPermissions());
        return userService.createUser(
                input.getName(),
                input.getEmail(),
                input.getPassword(),
                input.getIsAuditor(),
                perms,
                input.getIsCoach(),         // NUEVO
                input.getAssignedCoachId()  // NUEVO
        );
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        SecurityUtils.requireMutate(Module.USERS);
        userService.delete(id);
        return true;
    }

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

    // ===== NUEVO: activar/desactivar Coach (con XOR dentro del service) =====
    @MutationMapping
    public UserDTO setCoach(@Argument Long userId, @Argument Boolean coach) {
        SecurityUtils.requireMutate(Module.USERS);
        return userService.setCoach(userId, coach != null && coach);
    }

    // ===== NUEVO: asignar un Coach a un usuario =====
    @MutationMapping
    public UserDTO setUserCoach(@Argument Long userId, @Argument Long coachId) {
        SecurityUtils.requireMutate(Module.USERS);
        return userService.setUserCoach(userId, coachId);
    }

    // ===== NUEVO: registrar un usuario (sin asignar permisos ni coach) =====

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

    @QueryMapping
    public UserPageDTO usersPage(@Argument("page") com.tarea.resolvers.inputs.PageRequestInput page) {
        SecurityUtils.requireView(Module.USERS);
        var pageable = (page == null) ? org.springframework.data.domain.PageRequest.of(0,20) : page.toPageable();
        return userService.pageUsers(pageable);
    }


@QueryMapping
public UserPageDTO myCoacheesPage(@Argument("page") com.tarea.resolvers.inputs.PageRequestInput page) {
    Long me = com.tarea.security.SecurityUtils.userId();
    UserDTO meDto = userService.getById(me);
    if (!Boolean.TRUE.equals(meDto.getIsCoach())) {
        throw new AccessDeniedException("Forbidden");
    }
    var pageable = (page == null) ? PageRequest.of(0,20) : page.toPageable();
    return userService.pageCoachees(me, pageable);
}

@QueryMapping
public UserPageDTO coacheesPage(@Argument Long coachId,
                                @Argument("page") com.tarea.resolvers.inputs.PageRequestInput page) {
    Long me = null;
    try { me = com.tarea.security.SecurityUtils.userId(); } catch (Exception ignore) {}

    if (me != null) {
        UserDTO meDto = userService.getById(me);
        if (Boolean.TRUE.equals(meDto.getIsCoach()) && coachId.equals(me)) {
            var pageable = (page == null) ? PageRequest.of(0,20) : page.toPageable();
            return userService.pageCoachees(coachId, pageable);
        }
    }
    com.tarea.security.SecurityUtils.requireView(com.tarea.models.Module.USERS);
    var pageable = (page == null) ? PageRequest.of(0,20) : page.toPageable();
    return userService.pageCoachees(coachId, pageable);
}







}
