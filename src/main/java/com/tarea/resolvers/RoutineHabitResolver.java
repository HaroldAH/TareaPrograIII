package com.tarea.resolvers;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.resolvers.inputs.RoutineHabitInput;
import com.tarea.services.RoutineHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoutineHabitResolver {

    private final RoutineHabitService service;

    public RoutineHabitResolver(RoutineHabitService service) {
        this.service = service;
    }

    @QueryMapping
    public List<RoutineHabitDTO> getRoutineHabitsByRoutineId(@Argument Long routineId) {
        return service.getByRoutineId(routineId);
    }

    @MutationMapping
    public RoutineHabitDTO createRoutineHabit(@Argument RoutineHabitInput input) {
        return service.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteRoutineHabit(@Argument Long routineId, @Argument Long habitId) {
        service.delete(routineId, habitId);
        return true;
    }

    private RoutineHabitDTO toDTO(RoutineHabitInput in) {
        RoutineHabitDTO dto = new RoutineHabitDTO();
        dto.setRoutineId(in.getRoutineId());
        dto.setHabitId(in.getHabitId());
        dto.setOrderInRoutine(in.getOrderInRoutine());
        dto.setTargetTimeInRoutine(in.getTargetTimeInRoutine());
        dto.setNotes(in.getNotes());
        return dto;
    }
}
