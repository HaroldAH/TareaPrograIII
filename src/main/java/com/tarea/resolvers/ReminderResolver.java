package com.tarea.resolvers;

import com.tarea.dtos.ReminderDTO;
import com.tarea.dtos.ReminderListDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.ReminderInput;
import com.tarea.services.ReminderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class ReminderResolver {

    private final ReminderService reminderService;

    public ReminderResolver(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    /* ================= QUERIES (CONSULT) ================= */

    @QueryMapping
    public List<ReminderDTO> getAllReminders() {
        requireView(Module.REMINDERS);
        return reminderService.getAll();
    }

    @QueryMapping
    public ReminderDTO getReminderById(@Argument Long id) {
        requireView(Module.REMINDERS);
        return reminderService.getById(id);
    }

    @QueryMapping
    public List<ReminderDTO> getRemindersByUser(@Argument Long userId) {
        requireView(Module.REMINDERS);
        return reminderService.getByUserId(userId);
    }

    @QueryMapping
    public List<ReminderDTO> getMyReminders(Authentication auth) {
        requireView(Module.REMINDERS);
        Long me = Long.valueOf(auth.getName());
        return reminderService.getByUserId(me);
    }

    @QueryMapping
    public List<ReminderListDTO> getMyReminderList(Authentication auth) {
        requireView(Module.REMINDERS);
        Long me = Long.valueOf(auth.getName());
        return reminderService.getByUserId(me).stream().map(reminder -> {
            ReminderListDTO dto = new ReminderListDTO();
            dto.setId(reminder.getId());
            dto.setFrequency(reminder.getFrequency());

            HabitActivityListDTO habitDto = new HabitActivityListDTO();
            habitDto.setId(reminder.getHabitId());
            var habitOpt = reminderService.getHabitById(reminder.getHabitId());
            habitOpt.ifPresent(habit -> {
                habitDto.setName(habit.getName());
                habitDto.setCategory(habit.getCategory());
            });

            dto.setHabit(habitDto);
            return dto;
        }).toList();
    }

    /* ================= MUTATIONS (MUTATE) ================= */

    @MutationMapping
    public ReminderDTO createReminder(@Argument("input") ReminderInput input, Authentication auth) {
        requireMutate(Module.REMINDERS);
        Long me = Long.valueOf(auth.getName());
        ReminderDTO dto = toDTO(input);
        dto.setUserId(me); // fuerza el dueño al del token
        return reminderService.save(dto);
    }

    @MutationMapping
    public Boolean deleteReminder(@Argument Long id) {
        requireMutate(Module.REMINDERS);
        reminderService.delete(id);
        return true;
    }

    @MutationMapping
    public ReminderDTO createMyReminder(@Argument("input") ReminderInput input, Authentication auth) {
        requireMutate(Module.REMINDERS);
        Long me = Long.valueOf(auth.getName());
        ReminderDTO dto = toDTO(input);
        dto.setUserId(me);
        return reminderService.save(dto);
    }

    /* ================= Mapper ================= */

    private ReminderDTO toDTO(ReminderInput input) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(input.getId());
        dto.setHabitId(input.getHabitId());
        dto.setTime(input.getTime());
        dto.setFrequency(input.getFrequency());
        return dto;
    }
}
