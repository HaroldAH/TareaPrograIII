package com.tarea.resolvers;

import com.tarea.dtos.HabitActivityDTO;
import com.tarea.resolvers.inputs.HabitActivityInput;
import com.tarea.services.HabitActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HabitActivityResolver {

    private final HabitActivityService habitActivityService;

    public HabitActivityResolver(HabitActivityService habitActivityService) {
        this.habitActivityService = habitActivityService;
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<HabitActivityDTO> getAllHabitActivities() {
        return habitActivityService.getAll();
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public HabitActivityDTO getHabitActivityById(@Argument Long id) {
        return habitActivityService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    @MutationMapping
    public HabitActivityDTO createHabitActivity(@Argument HabitActivityInput input) {
        return habitActivityService.save(toDTO(input));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    @MutationMapping
    public Boolean deleteHabitActivity(@Argument Long id) {
        habitActivityService.delete(id);
        return true;
    }

    private HabitActivityDTO toDTO(HabitActivityInput input) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(input.getId());
        dto.setName(input.getName());
        dto.setCategory(input.getCategory());
        dto.setDescription(input.getDescription());
        dto.setDuration(input.getDuration());
        dto.setTargetTime(input.getTargetTime());
        dto.setNotes(input.getNotes());
        dto.setIsFavorite(input.getIsFavorite());
        return dto;
    }
}
