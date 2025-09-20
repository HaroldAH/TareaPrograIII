package com.tarea.resolvers;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.Module;
import com.tarea.models.User;
import com.tarea.repositories.UserRepository;
import com.tarea.resolvers.inputs.FavoriteHabitInput;
import com.tarea.services.FavoriteHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class FavoriteHabitResolver {

    private final FavoriteHabitService service;
    private final UserRepository userRepository;

    public FavoriteHabitResolver(FavoriteHabitService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    /* =================== QUERIES (CONSULT) =================== */

    @QueryMapping
    public List<FavoriteHabitDTO> getAllFavoriteHabits() {
        requireView(Module.HABITS);
        return service.getAll();
    }

    @QueryMapping
    public FavoriteHabitDTO getFavoriteHabitById(@Argument Long id) {
        requireView(Module.HABITS);
        return service.getById(id);
    }

    @QueryMapping
    public List<HabitActivityListDTO> getFavoriteHabitsListByUser() {
        requireView(Module.HABITS);
        Long userId = getAuthenticatedUserId();
        return service.getFavoriteHabitsListByUser(userId);
    }

    @QueryMapping
    public List<HabitActivityListDTO> getFavoriteHabitsByCategory(@Argument String category) {
        requireView(Module.HABITS);
        Long userId = getAuthenticatedUserId();
        return service.getFavoriteHabitsByCategory(userId, category);
    }

    @QueryMapping
    public HabitActivityDTO getFavoriteHabitDetailByName(@Argument String name) {
        requireView(Module.HABITS);
        Long userId = getAuthenticatedUserId();
        return service.getFavoriteHabitDetailByName(userId, name);
    }

    /* =================== MUTATIONS (MUTATE) =================== */

    @MutationMapping
    public FavoriteHabitDTO createFavoriteHabit(@Argument FavoriteHabitInput input) {
        requireMutate(Module.HABITS);
        Long userId = getAuthenticatedUserId();

        FavoriteHabitDTO dto = toDTO(input);
        dto.setUserId(userId); // fuerza el dueño al del token
        return service.save(dto);
    }

    @MutationMapping
    public Boolean deleteFavoriteHabit(@Argument Long id) {
        requireMutate(Module.HABITS);
        service.delete(id);
        return true;
    }

    /* =================== Helpers =================== */

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new RuntimeException("No autenticado");
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof User u) return u.getId();
        // fallback: algunos setups ponen el subject como String
        try { return Long.valueOf(auth.getName()); }
        catch (NumberFormatException e) {
            String email = auth.getName();
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
    }

    private FavoriteHabitDTO toDTO(FavoriteHabitInput in) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(in.getId());
        dto.setHabitId(in.getHabitId());
        return dto;
    }
}
