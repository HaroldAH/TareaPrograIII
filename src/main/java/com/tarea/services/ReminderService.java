package com.tarea.services;

import com.tarea.dtos.ReminderDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Reminder;
import com.tarea.models.User;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.ReminderRepository;
import com.tarea.repositories.UserRepository;
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
        return reminderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReminderDTO getById(Long id) {
        return reminderRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<ReminderDTO> getByUserId(Long userId) {
        return reminderRepository.findByUser_Id(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
public ReminderDTO save(ReminderDTO dto) {
    // Dejamos claro que si llega null, es culpa del resolver, no del user
    if (dto.getUserId() == null) {
        throw new IllegalStateException("El userId debe ser asignado en el Resolver antes de llamar al Service.");
    }
    if (dto.getHabitId() == null) {
        throw new IllegalArgumentException("habitId es obligatorio.");
    }

    User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));

    Habitactivity habit = habitActivityRepository.findById(dto.getHabitId())
            .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

    final Reminder entity;
    if (dto.getId() != null) {
        // UPDATE: debe existir
        entity = reminderRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Reminder no encontrado: " + dto.getId()));
    } else {
        // CREATE: no setear id manualmente si es AUTO_INCREMENT
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
        reminderRepository.deleteById(id);
    }

    private ReminderDTO toDTO(Reminder reminder) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(reminder.getId());
        dto.setUserId(reminder.getUser().getId()); // <-- Asegúrate de esto
        dto.setHabitId(reminder.getHabit().getId()); // <-- Y esto
        dto.setTime(reminder.getTime());
        dto.setFrequency(reminder.getFrequency());
        return dto;
    }

    public Optional<Habitactivity> getHabitById(Long habitId) {
        if (habitId == null) return Optional.empty();
        return habitActivityRepository.findById(habitId);
    }
}
