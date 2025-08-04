package com.tarea.services;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.models.Completedactivity;
import com.tarea.models.Habit;
import com.tarea.models.Progresslog;
import com.repositories.CompletedActivityRepository;
import com.repositories.HabitRepository;
import com.repositories.ProgressLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompletedActivityService {

    private final CompletedActivityRepository completedActivityRepository;
    private final ProgressLogRepository progressLogRepository;
    private final HabitRepository habitRepository;

    public CompletedActivityService(CompletedActivityRepository completedActivityRepository,
                                    ProgressLogRepository progressLogRepository,
                                    HabitRepository habitRepository) {
        this.completedActivityRepository = completedActivityRepository;
        this.progressLogRepository = progressLogRepository;
        this.habitRepository = habitRepository;
    }

    public List<CompletedActivityDTO> getAll() {
        return completedActivityRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CompletedActivityDTO getById(Long id) {
        return completedActivityRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CompletedActivityDTO save(CompletedActivityDTO dto) {
        Completedactivity entity = new Completedactivity();
        entity.setId(dto.getId());
        entity.setCompletedAt(dto.getCompletedAt());
        entity.setNotes(dto.getNotes());

        Progresslog progresslog = progressLogRepository.findById(dto.getProgressLogId())
                .orElseThrow(() -> new IllegalArgumentException("ProgressLog not found: " + dto.getProgressLogId()));
        entity.setProgressLog(progresslog);

        Habit habit = habitRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + dto.getHabitId()));
        entity.setHabit(habit);

        return toDTO(completedActivityRepository.save(entity));
    }

    public void delete(Long id) {
        completedActivityRepository.deleteById(id);
    }

    private CompletedActivityDTO toDTO(Completedactivity entity) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(entity.getId());
        dto.setProgressLogId(entity.getProgressLog().getId());
        dto.setHabitId(entity.getHabit().getId());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setNotes(entity.getNotes());
        return dto;
    }
}
