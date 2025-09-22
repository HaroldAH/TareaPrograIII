package com.tarea.services;

import com.tarea.dtos.ReminderDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;
import com.tarea.models.Reminder;
import com.tarea.models.User;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.ReminderRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.InputSanitizationUtils;
import com.tarea.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final HabitActivityRepository habitActivityRepository;

    public ReminderService(ReminderRepository reminderRepository,
                           UserRepository userRepository,
                           HabitActivityRepository habitActivityRepository) {
        this.reminderRepository = reminderRepository;
        this.userRepository = userRepository;
        this.habitActivityRepository = habitActivityRepository;
    }

 
    public List<ReminderDTO> getAll() {
        SecurityUtils.requireView(Module.REMINDERS);  
        return reminderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

 
    public ReminderDTO getById(Long id) {
        return reminderRepository.findById(id)
                .map(r -> {
                    Long owner = r.getUser().getId();
                    SecurityUtils.requireSelfOrView(owner, Module.REMINDERS);  
                    return toDTO(r);
                })
                .orElse(null);
    }

 
    public List<ReminderDTO> getByUserId(Long userId) {
        SecurityUtils.requireSelfOrView(userId, Module.REMINDERS);  
        return reminderRepository.findByUser_Id(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReminderDTO save(ReminderDTO dto) {
        InputSanitizationUtils.validateAllStringFields(dto);
        SecurityUtils.forbidAuditorWrites();                          

        Long me = SecurityUtils.userId();
        Long targetUserId = (dto.getUserId() != null) ? dto.getUserId() : me;

         
        SecurityUtils.requireSelfOrMutate(targetUserId, Module.REMINDERS);  

        if (dto.getHabitId() == null) {
            throw new IllegalArgumentException("habitId es obligatorio.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + targetUserId));

        Habitactivity habit = habitActivityRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

        final Reminder entity;
        if (dto.getId() != null) {
            entity = reminderRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Reminder no encontrado: " + dto.getId()));
             
            SecurityUtils.requireSelfOrMutate(entity.getUser().getId(), Module.REMINDERS);
        } else {
            entity = new Reminder();
        }

        entity.setUser(user);
        entity.setHabit(habit);
        entity.setTime(dto.getTime());
        entity.setFrequency(dto.getFrequency());

        Reminder saved = reminderRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        SecurityUtils.forbidAuditorWrites();                          

        Reminder r = reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder no encontrado: " + id));
         
        SecurityUtils.requireSelfOrMutate(r.getUser().getId(), Module.REMINDERS);
        reminderRepository.delete(r);
    }

    private ReminderDTO toDTO(Reminder reminder) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(reminder.getId());
        dto.setUserId(reminder.getUser().getId());
        dto.setHabitId(reminder.getHabit().getId());
        dto.setTime(reminder.getTime());
        dto.setFrequency(reminder.getFrequency());
        return dto;
    }

    public Optional<Habitactivity> getHabitById(Long habitId) {
        if (habitId == null) return Optional.empty();
        return habitActivityRepository.findById(habitId);
    }
}
