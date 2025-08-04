package com.tarea.services;

import com.tarea.dtos.RoutineActivityDTO;
import com.tarea.models.Habit;
import com.tarea.models.Routine;
import com.tarea.models.Routineactivity;
import com.repositories.HabitRepository;
import com.repositories.RoutineActivityRepository;
import com.repositories.RoutineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineActivityService {

    private final RoutineActivityRepository routineActivityRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;

    public RoutineActivityService(RoutineActivityRepository routineActivityRepository,
                                  RoutineRepository routineRepository,
                                  HabitRepository habitRepository) {
        this.routineActivityRepository = routineActivityRepository;
        this.routineRepository = routineRepository;
        this.habitRepository = habitRepository;
    }

    public List<RoutineActivityDTO> getAll() {
        return routineActivityRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RoutineActivityDTO getById(Long id) {
        return routineActivityRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public RoutineActivityDTO save(RoutineActivityDTO dto) {
        Routineactivity entity = new Routineactivity();
        entity.setId(dto.getId());
        entity.setDuration(dto.getDuration());
        entity.setTargetTime(dto.getTargetTime());
        entity.setNotes(dto.getNotes());

        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + dto.getRoutineId()));
        Habit habit = habitRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + dto.getHabitId()));

        entity.setRoutine(routine);
        entity.setHabit(habit);

        return toDTO(routineActivityRepository.save(entity));
    }

    public void delete(Long id) {
        routineActivityRepository.deleteById(id);
    }

    private RoutineActivityDTO toDTO(Routineactivity entity) {
        RoutineActivityDTO dto = new RoutineActivityDTO();
        dto.setId(entity.getId());
        dto.setRoutineId(entity.getRoutine().getId());
        dto.setHabitId(entity.getHabit().getId());
        dto.setDuration(entity.getDuration());
        dto.setTargetTime(entity.getTargetTime());
        dto.setNotes(entity.getNotes());
        return dto;
    }
}
