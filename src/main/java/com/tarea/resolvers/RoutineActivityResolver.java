package com.tarea.resolvers;

import com.tarea.dtos.RoutineActivityDTO;
import com.tarea.resolvers.inputs.RoutineActivityInput;
import com.tarea.services.RoutineActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class RoutineActivityResolver {

    private final RoutineActivityService routineActivityService;

    public RoutineActivityResolver(RoutineActivityService routineActivityService) {
        this.routineActivityService = routineActivityService;
    }

    @QueryMapping
    public List<RoutineActivityDTO> getAllRoutineActivities() {
        return routineActivityService.getAll();
    }

    @QueryMapping
    public RoutineActivityDTO getRoutineActivityById(@Argument Long id) {
        return routineActivityService.getById(id);
    }

    @MutationMapping
    public RoutineActivityDTO createRoutineActivity(@Argument RoutineActivityInput input) {
        return routineActivityService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteRoutineActivity(@Argument Long id) {
        routineActivityService.delete(id);
        return true;
    }

    private RoutineActivityDTO toDTO(RoutineActivityInput input) {
        RoutineActivityDTO dto = new RoutineActivityDTO();
        dto.setId(input.getId());
        dto.setRoutineId(input.getRoutineId());
        dto.setHabitId(input.getHabitId());
        dto.setDuration(input.getDuration());
        dto.setTargetTime(input.getTargetTime());
        dto.setNotes(input.getNotes());
        return dto;
    }
}