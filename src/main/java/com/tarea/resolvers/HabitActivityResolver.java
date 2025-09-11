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

    private final HabitActivityService service;

    public HabitActivityResolver(HabitActivityService service) {
        this.service = service;
    }

    // Listar/ver: abierto a todos los roles autenticados (o incluso sin auth si prefieres)
    @QueryMapping
    public List<HabitActivityDTO> getAllHabitActivities() {
        return service.getAll();
    }

    @QueryMapping
    public HabitActivityDTO getHabitActivityById(@Argument Long id) {
        return service.getById(id);
    }

    // Crear/actualizar: solo staff (ADMIN, COACH). Auditor NO.
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    @MutationMapping
    public HabitActivityDTO createHabitActivity(@Argument HabitActivityInput input) {
        return service.save(toDTO(input));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    @MutationMapping
    public Boolean deleteHabitActivity(@Argument Long id) {
        service.delete(id);
        return true;
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
        dto.setIsFavorite(in.getIsFavorite());
        return dto;
    }
}