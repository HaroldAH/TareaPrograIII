package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/** Reglas:
 * - Propio usuario: puede crear/borrar sin PROGRESS:MUTATE.
 * - Otros usuarios: requiere PROGRESS:MUTATE (valida el service).
 * - "My..." no exigen VIEW; globales sí (en el service).
 */
@Controller
public class CompletedActivityResolver {

    private final CompletedActivityService service;

    public CompletedActivityResolver(CompletedActivityService service) {
        this.service = service;
    }

    /* =================== QUERIES =================== */

    @QueryMapping
    public List<CompletedActivityDTO> getAllCompletedActivities() {
        return service.getAll();
    }

    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        return service.getById(id);
    }

    @QueryMapping
    public List<CompletedActivityDTO> getCompletedActivitiesByUser(@Argument Long userId,
                                                                   @Argument String startDate,
                                                                   @Argument String endDate) {
        return service.getByUser(userId, startDate, endDate);
    }

    @QueryMapping
    public List<CompletedActivityDTO> getMyCompletedActivities(@Argument String startDate,
                                                               @Argument String endDate) {
        return service.getMine(startDate, endDate);
    }

    @QueryMapping
    public List<CompletedDayDTO> getMyCompletedActivitiesPerDay(@Argument String startDate,
                                                                @Argument String endDate) {
        return service.getMinePerDay(startDate, endDate);
    }

    @QueryMapping
    public List<CompletedWeekDTO> getMyCompletedActivitiesPerWeek(@Argument String startDate,
                                                                  @Argument String endDate) {
        return service.getMinePerWeek(startDate, endDate);
    }

    @QueryMapping
    public CompletedDayDTO getMyCompletedActivitiesOnDay(@Argument String date) {
        return service.getMineOnDay(date);
    }

    /* =================== MUTATIONS =================== */

    @MutationMapping
    public CompletedActivityDTO createCompletedActivity(@Argument("input") CompletedActivityInput input) {
        return service.save(fromInput(input));
    }

    @MutationMapping
    public Boolean deleteCompletedActivity(@Argument Long id) {
        service.delete(id);
        return true;
    }

    /* =================== Mapper =================== */
    private CompletedActivityDTO fromInput(CompletedActivityInput in) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(in.getId());
        dto.setUserId(in.getUserId());        // si viene null, el service usará el del token
        dto.setRoutineId(in.getRoutineId());
        dto.setHabitId(in.getHabitId());
        dto.setDate(in.getDate());            // "YYYY-MM-DD"
        dto.setCompletedAt(in.getCompletedAt()); // "HH:mm"
        dto.setNotes(in.getNotes());
        return dto;
    }
}
