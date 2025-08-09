package com.tarea.resolvers;

import com.tarea.dtos.GuideHabitDTO;
import com.tarea.resolvers.inputs.GuideHabitInput;
import com.tarea.services.GuideHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GuideHabitResolver {

    private final GuideHabitService guideHabitService;

    public GuideHabitResolver(GuideHabitService guideHabitService) {
        this.guideHabitService = guideHabitService;
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<GuideHabitDTO> getAllGuideHabits() {
        return guideHabitService.getAll();
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public GuideHabitDTO getGuideHabitById(@Argument Long guideId, @Argument Long habitId) {
        return guideHabitService.getById(guideId, habitId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    @MutationMapping
    public GuideHabitDTO createGuideHabit(@Argument GuideHabitInput input) {
        return guideHabitService.save(toDTO(input));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    @MutationMapping
    public Boolean deleteGuideHabit(@Argument Long guideId, @Argument Long habitId) {
        guideHabitService.delete(guideId, habitId);
        return true;
    }

    private GuideHabitDTO toDTO(GuideHabitInput input) {
        GuideHabitDTO dto = new GuideHabitDTO();
        dto.setGuideId(input.getGuideId());
        dto.setHabitId(input.getHabitId());
        return dto;
    }
}
