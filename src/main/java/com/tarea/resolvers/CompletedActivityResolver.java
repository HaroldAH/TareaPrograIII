package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CompletedActivityResolver {

    private final CompletedActivityService completedActivityService;

    public CompletedActivityResolver(CompletedActivityService completedActivityService) {
        this.completedActivityService = completedActivityService;
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<CompletedActivityDTO> getAllCompletedActivities() {
        return completedActivityService.getAll();
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        return completedActivityService.getById(id);
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public CompletedActivityDTO createCompletedActivity(@Argument CompletedActivityInput input) {
        return completedActivityService.save(toDTO(input));
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public Boolean deleteCompletedActivity(@Argument Long id) {
        completedActivityService.delete(id);
        return true;
    }

    private CompletedActivityDTO toDTO(CompletedActivityInput input) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(input.getId());
        dto.setUserId(input.getUserId());
        dto.setRoutineId(input.getRoutineId());
        dto.setHabitId(input.getHabitId());
        dto.setDate(input.getDate());
        dto.setCompletedAt(input.getCompletedAt());
        dto.setIsCompleted(input.getIsCompleted());
        dto.setNotes(input.getNotes());
        return dto;
    }
}
