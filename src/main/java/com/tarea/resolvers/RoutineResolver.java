package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.RoutineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoutineResolver {

    private final RoutineService routineService;

    public RoutineResolver(RoutineService routineService) {
        this.routineService = routineService;
    }

 

 
    @QueryMapping
    public List<RoutineDTO> getAllRoutines() {
        SecurityUtils.requireView(Module.ROUTINES);
        return routineService.getAll();
    }

 
    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        return routineService.getById(id);
    }

 
    @QueryMapping
    public List<RoutineDTO> getRoutinesByUser(@Argument Long userId) {
        return routineService.getByUserId(userId);
    }

 
    @QueryMapping
    public List<RoutineDTO> getMyRoutines() {
        Long me = SecurityUtils.userId();
        return routineService.getByUserId(me);
    }

 
    @QueryMapping
    public RoutineDetailDTO getRoutineDetail(@Argument Long id) {
        return routineService.getRoutineDetail(id);
    }

 

 
    @MutationMapping
    public RoutineDTO createRoutine(@Argument("input") RoutineInput input) {
        if (input.getUserId() == null) {
            input.setUserId(SecurityUtils.userId());  
        }
        return routineService.save(toDTO(input));
    }

 
    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) {
        routineService.delete(id);
        return true;
    }

 

    private RoutineDTO toDTO(RoutineInput input) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(input.getId());
        dto.setTitle(input.getTitle());
        dto.setUserId(input.getUserId());
        dto.setDaysOfWeek(input.getDaysOfWeek());
        return dto;
    }
}
