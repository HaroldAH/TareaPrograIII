package com.tarea.resolvers;

import com.tarea.resolvers.inputs.HabitInput;
import com.tarea.dtos.HabitDTO;
import com.tarea.services.HabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class HabitResolver {

    private final HabitService habitService;

    public HabitResolver(HabitService habitService) {
        this.habitService = habitService;
    }

    @QueryMapping
    public List<HabitDTO> getAllHabits() {
        return habitService.getAll();
    }

    @QueryMapping
    public HabitDTO getHabitById(@Argument Long id) {
        return habitService.getById(id);
    }

    @MutationMapping
    public HabitDTO createHabit(@Argument HabitInput input) {
        return habitService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteHabit(@Argument Long id) {
        habitService.delete(id);
        return true;
    }

    private HabitDTO toDTO(HabitInput input) {
        HabitDTO dto = new HabitDTO();
        dto.setId(input.getId());
        dto.setName(input.getName());
        dto.setCategory(input.getCategory());
        dto.setDescription(input.getDescription());
        return dto;
    }
}