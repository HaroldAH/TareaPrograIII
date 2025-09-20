package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class CompletedActivityResolver {

    private final CompletedActivityService service;

    public CompletedActivityResolver(CompletedActivityService service) {
        this.service = service;
    }

    /* =================== QUERIES (CONSULT) =================== */

    @QueryMapping
    public List<CompletedActivityDTO> getAllCompletedActivities() {
        requireView(Module.PROGRESS);
        return service.getAll();
    }

    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        requireView(Module.PROGRESS);
        return service.getById(id);
    }

    @QueryMapping
    public List<CompletedActivityDTO> getCompletedActivitiesByUser(@Argument Long userId,
                                                                   @Argument String startDate,
                                                                   @Argument String endDate) {
        requireView(Module.PROGRESS);
        return service.getByUser(userId, startDate, endDate);
    }

    /* “My …” siguen existiendo, pero igual requieren permiso de consulta del módulo */
    @QueryMapping
    public List<CompletedActivityDTO> getMyCompletedActivities(@Argument String startDate,
                                                               @Argument String endDate,
                                                               Authentication auth) {
        requireView(Module.PROGRESS);
        Long me = Long.valueOf(auth.getName());
        return service.getByUser(me, startDate, endDate);
    }

    @QueryMapping
    public List<CompletedDayDTO> getMyCompletedActivitiesPerDay(@Argument String startDate,
                                                                @Argument String endDate,
                                                                Authentication auth) {
        requireView(Module.PROGRESS);
        Long me = Long.valueOf(auth.getName());
        return service.getCompletedByUserPerDay(me, startDate, endDate);
    }

    @QueryMapping
    public List<CompletedWeekDTO> getMyCompletedActivitiesPerWeek(@Argument String startDate,
                                                                  @Argument String endDate,
                                                                  Authentication auth) {
        requireView(Module.PROGRESS);
        Long me = Long.valueOf(auth.getName());
        return service.getCompletedByUserPerWeek(me, startDate, endDate);
    }

    @QueryMapping
    public CompletedDayDTO getMyCompletedActivitiesOnDay(@Argument String date,
                                                         Authentication auth) {
        requireView(Module.PROGRESS);
        Long me = Long.valueOf(auth.getName());
        return service.getCompletedByUserOnDay(me, date);
    }

    /* =================== MUTATIONS (MUTATE) =================== */

    @MutationMapping
    public CompletedActivityDTO createCompletedActivity(@Argument("input") CompletedActivityInput input,
                                                        Authentication auth) {
        requireMutate(Module.PROGRESS);

        // Conveniencia: si no viene userId, asume el del token
        if (input.getUserId() == null && auth != null) {
            input.setUserId(Long.valueOf(auth.getName()));
        }
        return service.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteCompletedActivity(@Argument Long id) {
        requireMutate(Module.PROGRESS);
        service.delete(id);
        return true;
    }

    /* =================== Mapper =================== */
    private CompletedActivityDTO toDTO(CompletedActivityInput in) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(in.getId());
        dto.setUserId(in.getUserId());
        dto.setRoutineId(in.getRoutineId());
        dto.setHabitId(in.getHabitId());
        dto.setDate(in.getDate());
        dto.setCompletedAt(in.getCompletedAt());
        dto.setNotes(in.getNotes());
        return dto;
    }
}
