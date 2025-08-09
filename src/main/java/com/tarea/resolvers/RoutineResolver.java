package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.services.RoutineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoutineResolver {

    private final RoutineService routineService;

    public RoutineResolver(RoutineService routineService) {
        this.routineService = routineService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public List<RoutineDTO> getAllRoutines() {
        return routineService.getAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        return routineService.getById(id);
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public RoutineDTO createRoutine(@Argument RoutineInput input) {
        return routineService.save(toDTO(input));
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) {
        routineService.delete(id);
        return true;
    }

    private RoutineDTO toDTO(RoutineInput input) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(input.getId());
        dto.setTitle(input.getTitle());
        dto.setUserId(input.getUserId());
        dto.setDaysOfWeek(input.getDaysOfWeek());
        return dto;
    }
}
