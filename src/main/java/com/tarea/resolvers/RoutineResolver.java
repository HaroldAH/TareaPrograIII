package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.services.RoutineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoutineResolver {

    private final RoutineService routineService;

    public RoutineResolver(RoutineService routineService) {
        this.routineService = routineService;
    }

    // Solo staff
    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public List<RoutineDTO> getAllRoutines() {
        return routineService.getAll();
    }

    // Solo staff
    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        return routineService.getById(id);
    }

    // Staff ve cualquiera; USER solo las suyas
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<RoutineDTO> getRoutinesByUser(@Argument Long userId, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));

        if (!isStaff && !String.valueOf(userId).equals(auth.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return routineService.getByUserId(userId);
    }

    // Mis rutinas
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<RoutineDTO> getMyRoutines(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return routineService.getByUserId(userId);
    }

    // Crear/Actualizar (AUDITOR no puede)
    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public RoutineDTO createRoutine(@Argument("input") RoutineInput input, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        Long me = Long.valueOf(auth.getName());

        if (!isStaff) {
            // UPDATE: verificar dueño de la rutina existente
            if (input.getId() != null) {
                RoutineDTO existing = routineService.getById(input.getId());
                if (existing == null) {
                    throw new IllegalArgumentException("Rutina no encontrada: " + input.getId());
                }
                if (!me.equals(existing.getUserId())) {
                    throw new org.springframework.security.access.AccessDeniedException("Forbidden");
                }
            }
            // CREATE: forzar/validar userId = yo
            if (input.getUserId() == null) {
                input.setUserId(me);
            } else if (!me.equals(input.getUserId())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }
        return routineService.save(toDTO(input));
    }

    // Borrar (AUDITOR no puede)
    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        if (!isStaff) {
            RoutineDTO r = routineService.getById(id);
            if (r == null) {
                throw new IllegalArgumentException("Rutina no encontrada: " + id);
            }
            if (!String.valueOf(r.getUserId()).equals(auth.getName())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }
        routineService.delete(id);
        return true;
    }

    // Mapper input -> DTO
    private RoutineDTO toDTO(RoutineInput input) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(input.getId());
        dto.setTitle(input.getTitle());
        dto.setUserId(input.getUserId());
        dto.setDaysOfWeek(input.getDaysOfWeek());
        return dto;
    }
}
