package com.tarea.resolvers;

import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.dtos.ReminderDTO;
import com.tarea.dtos.ReminderListDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.ReminderInput;
import com.tarea.security.SecurityUtils;
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

    /* ================= QUERIES ================= */

    /** Global: sólo staff con VIEW */
    @QueryMapping
    public List<ReminderDTO> getAllReminders() {
        SecurityUtils.requireView(Module.REMINDERS);
        return reminderService.getAll();
    }

    /** Ver uno: deja que el service haga owner-or-view */
    @QueryMapping
    public ReminderDTO getReminderById(@Argument Long id) {
        return reminderService.getById(id);
    }

    /** Por usuario: deja que el service haga owner-or-view */
    @QueryMapping
    public List<ReminderDTO> getRemindersByUser(@Argument Long userId) {
        return reminderService.getByUserId(userId);
    }

    /** Mis recordatorios: autoservicio */
    @QueryMapping
    public List<ReminderDTO> getMyReminders() {
        Long me = SecurityUtils.userId();
        return reminderService.getByUserId(me);
    }

    /** Mi lista para UI (card) */
    @QueryMapping
    public List<ReminderListDTO> getMyReminderList() {
        Long me = SecurityUtils.userId();
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

    /* ================= MUTATIONS ================= */

    /**
     * Crear/editar:
     * - Si input.userId == null ⇒ se asume dueño = token (autoservicio)
     * - Si viene userId ⇒ el service hará requireSelfOrMutate (terceros ⇒ requiere :RW)
     */
    @MutationMapping
    public ReminderDTO createReminder(@Argument("input") ReminderInput input) {
        ReminderDTO dto = toDTO(input);
        if (dto.getUserId() == null) {
            dto.setUserId(SecurityUtils.userId());
        }
        return reminderService.save(dto);
    }

    /** Azúcar explícito para “lo mío” */
    @MutationMapping
    public ReminderDTO createMyReminder(@Argument("input") ReminderInput input) {
        ReminderDTO dto = toDTO(input);
        dto.setUserId(SecurityUtils.userId()); // fuerza dueño = token
        return reminderService.save(dto);
    }

    /** Borrar: deja que el service haga self-or-mutate */
    @MutationMapping
    public Boolean deleteReminder(@Argument Long id) {
        reminderService.delete(id);
        return true;
    }

    /* ================= Mapper ================= */

   // en ReminderResolver
private ReminderDTO toDTO(ReminderInput input) {
    ReminderDTO dto = new ReminderDTO();
    dto.setId(input.getId());
    dto.setUserId(input.getUserId());   // 👈 ahora sí propagamos el userId enviado
    dto.setHabitId(input.getHabitId());
    dto.setTime(input.getTime());
    dto.setFrequency(input.getFrequency());
    return dto;
}


}
