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

    private final FavoriteHabitService favoriteHabitService;

    public FavoriteHabitResolver(FavoriteHabitService favoriteHabitService) {
        this.favoriteHabitService = favoriteHabitService;
    }

    @QueryMapping
    public List<FavoriteHabitDTO> getAllFavoriteHabits() {
        return favoriteHabitService.getAll();
    }

    @QueryMapping
    public FavoriteHabitDTO getFavoriteHabitById(@Argument Long userId, @Argument Long habitId) {
        return favoriteHabitService.getById(userId, habitId);
    }

    @MutationMapping
    public FavoriteHabitDTO createFavoriteHabit(@Argument FavoriteHabitInput input) {
        return favoriteHabitService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteFavoriteHabit(@Argument Long userId, @Argument Long habitId) {
        favoriteHabitService.delete(userId, habitId);
        return true;
    }

    private FavoriteHabitDTO toDTO(FavoriteHabitInput input) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setUserId(input.getUserId());
        dto.setHabitId(input.getHabitId());
        return dto;
    }
}