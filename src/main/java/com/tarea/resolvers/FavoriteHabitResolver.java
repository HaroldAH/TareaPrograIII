package com.tarea.resolvers;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.Module;
import com.tarea.repositories.UserRepository;
import com.tarea.resolvers.inputs.FavoriteHabitInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.FavoriteHabitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FavoriteHabitResolver {

    private final FavoriteHabitService service;
    @SuppressWarnings("unused")
    private final UserRepository userRepository;

    public FavoriteHabitResolver(FavoriteHabitService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    /* =================== QUERIES =================== */

    /** Lista global: sólo staff con VIEW (esto sí queda igual) */
    @QueryMapping
    public List<FavoriteHabitDTO> getAllFavoriteHabits() {
        SecurityUtils.requireView(Module.HABITS);
        return service.getAll();
    }

    /** Ver uno por id: deja que el service haga owner-or-view */
    @QueryMapping
    public FavoriteHabitDTO getFavoriteHabitById(@Argument Long id) {
        return service.getById(id);
    }

    /** Mis favoritos (autoservicio: sin exigir VIEW) */
    @QueryMapping
    public List<HabitActivityListDTO> getFavoriteHabitsListByUser() {
        Long me = SecurityUtils.userId();
        return service.getFavoriteHabitsListByUser(me);
    }

    /** Mis favoritos por categoría (autoservicio) */
    @QueryMapping
    public List<HabitActivityListDTO> getFavoriteHabitsByCategory(@Argument String category) {
        Long me = SecurityUtils.userId();
        return service.getFavoriteHabitsByCategory(me, category);
    }

    /** Detalle por nombre (autoservicio) */
    @QueryMapping
    public HabitActivityDTO getFavoriteHabitDetailByName(@Argument String name) {
        Long me = SecurityUtils.userId();
        return service.getFavoriteHabitDetailByName(me, name);
    }

    /* =================== MUTATIONS =================== */

    /** Crear favorito propio (sin exigir :RW; el service valida self-or-mutate para terceros) */
    @MutationMapping
    public FavoriteHabitDTO createFavoriteHabit(@Argument FavoriteHabitInput input) {
        FavoriteHabitDTO dto = toDTO(input);
        dto.setUserId(SecurityUtils.userId()); // fuerza dueño = token
        return service.save(dto);
    }

    /** Borrar (el service valida que seas dueño o que tengas :RW) */
    @MutationMapping
    public Boolean deleteFavoriteHabit(@Argument Long id) {
        service.delete(id);
        return true;
    }

    /* =================== Helpers =================== */

    private FavoriteHabitDTO toDTO(FavoriteHabitInput in) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(in.getId());
        dto.setHabitId(in.getHabitId());
        return dto;
    }
}
