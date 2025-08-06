package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.services.RoutineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoutineResolver {

    private final RoutineService routineService;

    public RoutineResolver(RoutineService routineService) {
        this.routineService = routineService;
    }

    @QueryMapping
    public List<RoutineDTO> getAllRoutines() {
        return routineService.getAll();
    }

    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        return routineService.getById(id);
    }

    @MutationMapping
    public RoutineDTO createRoutine(@Argument RoutineInput input) {
        return routineService.save(toDTO(input));
    }

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
