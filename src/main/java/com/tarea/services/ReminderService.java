package com.tarea.services;

import com.tarea.dtos.ReminderDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Reminder;
import com.tarea.models.User;
import com.repositories.HabitActivityRepository;
import com.repositories.ReminderRepository;
import com.repositories.UserRepository;
import org.springframework.stereotype.Service;

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

    public ReminderDTO save(ReminderDTO dto) {
        Reminder entity = new Reminder();
        entity.setId(dto.getId());

        Optional<User> user = userRepository.findById(dto.getUserId());
        Optional<Habitactivity> habit = habitActivityRepository.findById(dto.getHabitId());

        entity.setUser(user.orElse(null));
        entity.setHabit(habit.orElse(null));
        entity.setTime(dto.getTime());
        entity.setFrequency(dto.getFrequency());

        Reminder saved = reminderRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }

    private ReminderDTO toDTO(Reminder entity) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setHabitId(entity.getHabit() != null ? entity.getHabit().getId() : null);
        dto.setTime(entity.getTime());
        dto.setFrequency(entity.getFrequency());
        return dto;
    }
}
