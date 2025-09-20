package com.tarea.resolvers;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.RoutineHabitInput;
import com.tarea.services.RoutineHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class RoutineHabitResolver {

    private final RoutineHabitService service;

    public RoutineHabitResolver(RoutineHabitService service) {
        this.service = service;
    }

    /* ============ QUERIES (CONSULT) ============ */

    @QueryMapping
    public List<RoutineHabitDTO> getRoutineHabitsByRoutineId(@Argument Long routineId) {
        requireView(Module.ROUTINES);
        return service.getByRoutineId(routineId);
    }

    /* ============ MUTATIONS (MUTATE) ============ */

    @MutationMapping
    public RoutineHabitDTO createRoutineHabit(@Argument RoutineHabitInput input) {
        requireMutate(Module.ROUTINES);
        return service.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteRoutineHabit(@Argument Long routineId, @Argument Long habitId) {
        requireMutate(Module.ROUTINES);
        service.delete(routineId, habitId);
        return true;
    }

    /* ============ Mapper ============ */

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
