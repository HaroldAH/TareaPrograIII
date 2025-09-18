package com.tarea.resolvers;

import com.tarea.dtos.ReminderDTO;
import com.tarea.dtos.ReminderListDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.resolvers.inputs.ReminderInput;
import com.tarea.services.ReminderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReminderResolver {

    private final ReminderService reminderService;

    public ReminderResolver(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    // Solo staff
    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public List<ReminderDTO> getAllReminders() {
        return reminderService.getAll();
    }

    // Solo staff
    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public ReminderDTO getReminderById(@Argument Long id) {
        return reminderService.getById(id);
    }

    // USER ve solo lo suyo; staff puede ver cualquiera
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<ReminderDTO> getRemindersByUser(@Argument Long userId, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        if (!isStaff && !String.valueOf(userId).equals(auth.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return reminderService.getByUserId(userId);
    }

    // Conveniencia para el usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<ReminderDTO> getMyReminders(Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        return reminderService.getByUserId(me);
    }

    // Crear/actualizar: AUDITOR no puede; USER solo los suyos
    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public ReminderDTO createReminder(@Argument("input") ReminderInput input, Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        ReminderDTO dto = toDTO(input);
        dto.setUserId(me); // Siempre asigna el usuario autenticado
        return reminderService.save(dto);
    }

    // Borrar: AUDITOR no puede; USER solo los suyos
    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public Boolean deleteReminder(@Argument Long id, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        if (!isStaff) {
            ReminderDTO r = reminderService.getById(id);
            if (r == null) throw new IllegalArgumentException("Reminder no encontrado: " + id);
            if (!String.valueOf(r.getUserId()).equals(auth.getName())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }
        reminderService.delete(id);
        return true;
    }

    // Crear recordatorio forzando el usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public ReminderDTO createMyReminder(@Argument("input") ReminderInput input, Authentication auth) {
        Long me = Long.valueOf(auth.getName());
        ReminderDTO dto = toDTO(input);
        dto.setUserId(me); // Fuerza el usuario autenticado en el DTO
        return reminderService.save(dto);
    }

    // Obtener lista de recordatorios simplificada para el usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<ReminderListDTO> getMyReminderList(Authentication auth) {
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

    private ReminderDTO toDTO(ReminderInput input) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(input.getId());
        dto.setHabitId(input.getHabitId());
        dto.setTime(input.getTime());
        dto.setFrequency(input.getFrequency());
        return dto;
    }
}
