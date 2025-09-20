package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.services.RoutineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class RoutineResolver {

    private final RoutineService routineService;

    public RoutineResolver(RoutineService routineService) {
        this.routineService = routineService;
    }

    /* ============ QUERIES (CONSULT) ============ */

    @QueryMapping
    public List<RoutineDTO> getAllRoutines() {
        requireView(Module.ROUTINES);
        return routineService.getAll();
    }

    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        requireView(Module.ROUTINES);
        return routineService.getById(id);
    }

    @QueryMapping
    public List<RoutineDTO> getRoutinesByUser(@Argument Long userId) {
        requireView(Module.ROUTINES);
        return routineService.getByUserId(userId);
    }

    @QueryMapping
    public List<RoutineDTO> getMyRoutines(Authentication auth) {
        requireView(Module.ROUTINES);
        Long me = Long.valueOf(auth.getName());
        return routineService.getByUserId(me);
    }

    @QueryMapping
    public RoutineDetailDTO getRoutineDetail(@Argument Long id) {
        requireView(Module.ROUTINES);
        return routineService.getRoutineDetail(id);
    }

    /* ============ MUTATIONS (MUTATE) ============ */

    @MutationMapping
    public RoutineDTO createRoutine(@Argument("input") RoutineInput input, Authentication auth) {
        requireMutate(Module.ROUTINES);
        // Conveniencia: si no viene userId, usar el del token si está disponible
        if (input.getUserId() == null && auth != null) {
            try { input.setUserId(Long.valueOf(auth.getName())); } catch (NumberFormatException ignored) {}
        }
        return routineService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) {
        requireMutate(Module.ROUTINES);
        routineService.delete(id);
        return true;
    }

    /* ============ Mapper ============ */

    private RoutineDTO toDTO(RoutineInput input) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(input.getId());
        dto.setTitle(input.getTitle());
        dto.setUserId(input.getUserId());
        dto.setDaysOfWeek(input.getDaysOfWeek());
        return dto;
    }
}
