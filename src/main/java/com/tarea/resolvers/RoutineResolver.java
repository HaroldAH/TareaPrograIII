package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.security.SecurityUtils;
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

    /* ============ QUERIES ============ */

    /** Global: sólo staff con VIEW */
    @QueryMapping
    public List<RoutineDTO> getAllRoutines() {
        SecurityUtils.requireView(Module.ROUTINES);
        return routineService.getAll();
    }

    /** Ver una: deja que el service haga owner-or-view */
    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        return routineService.getById(id);
    }

    /** Por usuario: deja que el service haga owner-or-view */
    @QueryMapping
    public List<RoutineDTO> getRoutinesByUser(@Argument Long userId) {
        return routineService.getByUserId(userId);
    }

    /** Mis rutinas: autoservicio (sin requireView) */
    @QueryMapping
    public List<RoutineDTO> getMyRoutines() {
        Long me = SecurityUtils.userId();
        return routineService.getByUserId(me);
    }

    /** Detalle: deja que el service haga owner-or-view */
    @QueryMapping
    public RoutineDetailDTO getRoutineDetail(@Argument Long id) {
        return routineService.getRoutineDetail(id);
    }

    /* ============ MUTATIONS ============ */

    /** Crear/editar: autoservicio; el service valida self-or-mutate */
    @MutationMapping
    public RoutineDTO createRoutine(@Argument("input") RoutineInput input) {
        if (input.getUserId() == null) {
            input.setUserId(SecurityUtils.userId()); // dueño = token si no viene
        }
        return routineService.save(toDTO(input));
    }

    /** Borrar: deja que el service haga self-or-mutate */
    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) {
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
