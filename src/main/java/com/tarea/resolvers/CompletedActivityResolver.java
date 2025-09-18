package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CompletedActivityResolver {

    private final CompletedActivityService service;

    public CompletedActivityResolver(CompletedActivityService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public List<CompletedActivityDTO> getAllCompletedActivities() {
        return service.getAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        return service.getById(id);
    }

    // Extras recomendados
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<CompletedActivityDTO> getCompletedActivitiesByUser(@Argument Long userId,
                                                                   @Argument String startDate,
                                                                   @Argument String endDate,
                                                                   Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        if (!isStaff && !String.valueOf(userId).equals(auth.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return service.getByUser(userId, startDate, endDate);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<CompletedActivityDTO> getMyCompletedActivities(@Argument String startDate,
                                                               @Argument String endDate,
                                                               Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        return service.getByUser(me, startDate, endDate);
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public CompletedActivityDTO createCompletedActivity(@Argument("input") CompletedActivityInput input,
                                                        Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        Long me = Long.valueOf(auth.getName());

        if (!isStaff) {
            if (input.getId() != null) {
                CompletedActivityDTO existing = service.getById(input.getId());
                if (existing == null) throw new IllegalArgumentException("Registro no encontrado: " + input.getId());
                if (!me.equals(existing.getUserId())) {
                    throw new org.springframework.security.access.AccessDeniedException("Forbidden");
                }
            }
            if (input.getUserId() == null) input.setUserId(me);
            else if (!me.equals(input.getUserId())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }

        return service.save(toDTO(input));
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public Boolean deleteCompletedActivity(@Argument Long id, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));

        if (!isStaff) {
            CompletedActivityDTO existing = service.getById(id);
            if (existing == null) throw new IllegalArgumentException("Registro no encontrado: " + id);
            if (!String.valueOf(existing.getUserId()).equals(auth.getName())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }

        service.delete(id);
        return true;
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<CompletedDayDTO> getMyCompletedActivitiesPerDay(@Argument String startDate,
                                                           @Argument String endDate,
                                                           Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        return service.getCompletedByUserPerDay(me, startDate, endDate);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<CompletedWeekDTO> getMyCompletedActivitiesPerWeek(@Argument String startDate,
                                                             @Argument String endDate,
                                                             Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        return service.getCompletedByUserPerWeek(me, startDate, endDate);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public CompletedDayDTO getMyCompletedActivitiesOnDay(@Argument String date, Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        return service.getCompletedByUserOnDay(me, date);
    }

    private CompletedActivityDTO toDTO(CompletedActivityInput in) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(in.getId());
        dto.setUserId(in.getUserId());
        dto.setRoutineId(in.getRoutineId());
        dto.setHabitId(in.getHabitId());
        dto.setDate(in.getDate());
        dto.setCompletedAt(in.getCompletedAt());
        dto.setNotes(in.getNotes());
        return dto;
    }
}
