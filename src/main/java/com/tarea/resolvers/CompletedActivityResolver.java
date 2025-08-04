package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class CompletedActivityResolver {

    private final CompletedActivityService completedActivityService;

    public CompletedActivityResolver(CompletedActivityService completedActivityService) {
        this.completedActivityService = completedActivityService;
    }

    @QueryMapping
    public List<CompletedActivityDTO> getAllCompletedActivities() {
        return completedActivityService.getAll();
    }

    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        return completedActivityService.getById(id);
    }

    @MutationMapping
    public CompletedActivityDTO createCompletedActivity(@Argument CompletedActivityInput input) {
        return completedActivityService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteCompletedActivity(@Argument Long id) {
        completedActivityService.delete(id);
        return true;
    }

    private CompletedActivityDTO toDTO(CompletedActivityInput input) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(input.getId());
        dto.setProgressLogId(input.getProgressLogId());
        dto.setHabitId(input.getHabitId());
        dto.setCompletedAt(input.getCompletedAt());
        dto.setNotes(input.getNotes());
        return dto;
    }
}