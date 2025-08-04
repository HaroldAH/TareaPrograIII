package com.tarea.resolvers;

import com.tarea.dtos.ReminderDTO;
import com.tarea.resolvers.inputs.ReminderInput;
import com.tarea.services.ReminderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class ReminderResolver {

    private final ReminderService reminderService;

    public ReminderResolver(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @QueryMapping
    public List<ReminderDTO> getAllReminders() {
        return reminderService.getAll();
    }

    @QueryMapping
    public ReminderDTO getReminderById(@Argument Long id) {
        return reminderService.getById(id);
    }

    @MutationMapping
    public ReminderDTO createReminder(@Argument ReminderInput input) {
        return reminderService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteReminder(@Argument Long id) {
        reminderService.delete(id);
        return true;
    }

    private ReminderDTO toDTO(ReminderInput input) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(input.getId());
        dto.setUserId(input.getUserId());
        dto.setHabitId(input.getHabitId());
        dto.setTime(input.getTime());
        dto.setFrequency(input.getFrequency());
        return dto;
    }
}