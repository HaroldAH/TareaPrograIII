package com.tarea.resolvers;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.HabitActivityInput;
import com.tarea.services.HabitActivityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HabitActivityResolver {

    private final HabitActivityService service;

    public HabitActivityResolver(HabitActivityService service) {
        this.service = service;
    }

    @QueryMapping
    public List<HabitActivityDTO> getAllHabitActivities() {
        requireView(Module.HABITS);
        return service.getAll();
    }

    @QueryMapping
    public List<HabitActivityListDTO> getHabitActivitiesByCategory(@Argument String category) {
        requireView(Module.HABITS);
        return service.getByCategory(category);
    }

    @QueryMapping
    public HabitActivityDTO getHabitActivityById(@Argument Long id) {
        requireView(Module.HABITS);
        return service.getById(id);
    }

    @QueryMapping
    public HabitActivityDTO getHabitActivityByName(@Argument String name) {
        requireView(Module.HABITS);
        return service.getByName(name);
    }

    @MutationMapping
    public HabitActivityDTO createHabitActivity(@Argument HabitActivityInput input) {
        requireMutate(Module.HABITS);
        return service.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteHabitActivity(@Argument Long id) {
        requireMutate(Module.HABITS);
        service.delete(id);
        return true;
    }

    @QueryMapping
    public List<HabitActivityListDTO> getAllHabitActivitiesAsList() {
        requireView(Module.HABITS);
        return service.getAllAsList();
    }

    private HabitActivityDTO toDTO(HabitActivityInput in) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(in.getId());
        dto.setName(in.getName());
        dto.setCategory(in.getCategory());
        dto.setDescription(in.getDescription());
        dto.setDuration(in.getDuration());
        dto.setTargetTime(in.getTargetTime());
        dto.setNotes(in.getNotes());
        return dto;
    }
}
