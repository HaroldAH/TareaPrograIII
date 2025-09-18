package com.tarea.resolvers;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.User;
import com.tarea.resolvers.inputs.FavoriteHabitInput;
import com.tarea.services.FavoriteHabitService;
import com.tarea.repositories.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FavoriteHabitResolver {

    private final FavoriteHabitService service;
    private final UserRepository userRepository;

    public FavoriteHabitResolver(FavoriteHabitService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
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
        Long userId = getAuthenticatedUserId();
        // Aquí puedes crear el DTO y asignar el userId
        FavoriteHabitDTO dto = toDTO(input);
        dto.setUserId(userId);
        return service.save(dto);
    }

    @MutationMapping
    public Boolean deleteFavoriteHabit(@Argument Long id) {
        service.delete(id);
        return true;
    }

    @QueryMapping
    public List<HabitActivityListDTO> getFavoriteHabitsListByUser() {
        Long userId = getAuthenticatedUserId();
        return service.getFavoriteHabitsListByUser(userId);
    }

    @QueryMapping
    public List<HabitActivityListDTO> getFavoriteHabitsByCategory(@Argument String category) {
        Long userId = getAuthenticatedUserId();
        return service.getFavoriteHabitsByCategory(userId, category);
    }

    @QueryMapping
    public HabitActivityDTO getFavoriteHabitDetailByName(@Argument String name) {
        Long userId = getAuthenticatedUserId();
        return service.getFavoriteHabitDetailByName(userId, name);
    }

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        if (auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getId();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private FavoriteHabitDTO toDTO(FavoriteHabitInput in) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(in.getId());
        dto.setHabitId(in.getHabitId());
        return dto;
    }
}

