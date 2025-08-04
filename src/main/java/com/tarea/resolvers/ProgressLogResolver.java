package com.tarea.resolvers;

import com.tarea.dtos.ProgressLogDTO;
import com.tarea.resolvers.inputs.ProgressLogInput;
import com.tarea.services.ProgressLogService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ProgressLogResolver {

    private final ProgressLogService progressLogService;

    public ProgressLogResolver(ProgressLogService progressLogService) {
        this.progressLogService = progressLogService;
    }

    @QueryMapping
    public List<ProgressLogDTO> getAllProgressLogs() {
        return progressLogService.getAll();
    }

    @QueryMapping
    public ProgressLogDTO getProgressLogById(@Argument Long id) {
        return progressLogService.getById(id);
    }

    @MutationMapping
    public ProgressLogDTO createProgressLog(@Argument ProgressLogInput input) {
        return progressLogService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteProgressLog(@Argument Long id) {
        progressLogService.delete(id);
        return true;
    }

    private ProgressLogDTO toDTO(ProgressLogInput input) {
        ProgressLogDTO dto = new ProgressLogDTO();
        dto.setId(input.getId());
        dto.setUserId(input.getUserId());
        dto.setRoutineId(input.getRoutineId());
        dto.setDate(input.getDate() != null ? LocalDate.parse(input.getDate()) : null);
        return dto;
    }
}