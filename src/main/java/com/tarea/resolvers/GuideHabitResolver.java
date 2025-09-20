package com.tarea.resolvers;

import com.tarea.dtos.GuideHabitDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.GuideHabitInput;
import com.tarea.services.GuideHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class GuideHabitResolver {

    private final GuideHabitService guideHabitService;

    public GuideHabitResolver(GuideHabitService guideHabitService) {
        this.guideHabitService = guideHabitService;
    }

    /* =============== QUERIES (CONSULT) =============== */

    @QueryMapping
    public List<GuideHabitDTO> getAllGuideHabits() {
        requireView(Module.GUIDES);
        return guideHabitService.getAll();
    }

    @QueryMapping
    public GuideHabitDTO getGuideHabitById(@Argument Long guideId, @Argument Long habitId) {
        requireView(Module.GUIDES);
        return guideHabitService.getById(guideId, habitId);
    }

    /* =============== MUTATIONS (MUTATE) =============== */

    @MutationMapping
    public GuideHabitDTO createGuideHabit(@Argument GuideHabitInput input) {
        requireMutate(Module.GUIDES);
        return guideHabitService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteGuideHabit(@Argument Long guideId, @Argument Long habitId) {
        requireMutate(Module.GUIDES);
        guideHabitService.delete(guideId, habitId);
        return true;
    }

    /* =============== Mapper =============== */
    private GuideHabitDTO toDTO(GuideHabitInput input) {
        GuideHabitDTO dto = new GuideHabitDTO();
        dto.setGuideId(input.getGuideId());
        dto.setHabitId(input.getHabitId());
        return dto;
    }
}
