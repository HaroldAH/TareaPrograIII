package com.tarea.resolvers;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.resolvers.inputs.FavoriteHabitInput;
import com.tarea.services.FavoriteHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FavoriteHabitResolver {

    private final FavoriteHabitService service;

    public FavoriteHabitResolver(FavoriteHabitService service) {
        this.service = service;
    }

    @QueryMapping
    public List<FavoriteHabitDTO> getAllFavoriteHabits() {
        return service.getAll();
    }

    @QueryMapping
    public FavoriteHabitDTO getFavoriteHabitById(@Argument Long id) {
        return service.getById(id);
    }

    @MutationMapping
    public FavoriteHabitDTO createFavoriteHabit(@Argument FavoriteHabitInput input) {
        return service.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteFavoriteHabit(@Argument Long id) {
        service.delete(id);
        return true;
    }

    private FavoriteHabitDTO toDTO(FavoriteHabitInput in) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(in.getId());
        dto.setUserId(in.getUserId());
        dto.setHabitId(in.getHabitId());
        return dto;
    }
}

