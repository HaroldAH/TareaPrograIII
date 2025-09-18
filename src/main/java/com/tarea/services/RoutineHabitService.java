package com.tarea.services;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import com.tarea.models.Routine;
import com.tarea.models.Habitactivity;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.HabitActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineHabitService {

    private final RoutineHabitRepository routineHabitRepository;
    private final RoutineRepository routineRepository;
    private final HabitActivityRepository habitActivityRepository;

    public RoutineHabitService(RoutineHabitRepository routineHabitRepository,
                               RoutineRepository routineRepository,
                               HabitActivityRepository habitActivityRepository) {
        this.routineHabitRepository = routineHabitRepository;
        this.routineRepository = routineRepository;
        this.habitActivityRepository = habitActivityRepository;
    }

    public List<RoutineHabitDTO> getByRoutineId(Long routineId) {
        return routineHabitRepository.findByRoutine_Id(routineId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoutineHabitDTO save(RoutineHabitDTO dto) {
        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        Habitactivity habit = habitActivityRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

        RoutineHabitId id = new RoutineHabitId();
        id.setRoutineId(dto.getRoutineId());
        id.setHabitId(dto.getHabitId());

        RoutineHabit entity = routineHabitRepository.findById(id).orElse(new RoutineHabit());
        entity.setId(id);
        entity.setRoutine(routine);
        entity.setHabit(habit);
        entity.setOrderInRoutine(dto.getOrderInRoutine());
        entity.setTargetTimeInRoutine(dto.getTargetTimeInRoutine() != null ? LocalTime.parse(dto.getTargetTimeInRoutine()) : null);
        entity.setNotes(dto.getNotes());

        RoutineHabit saved = routineHabitRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long routineId, Long habitId) {
        RoutineHabitId id = new RoutineHabitId();
        id.setRoutineId(routineId);
        id.setHabitId(habitId);
        routineHabitRepository.deleteById(id);
    }

    private RoutineHabitDTO toDTO(RoutineHabit entity) {
        RoutineHabitDTO dto = new RoutineHabitDTO();
        dto.setRoutineId(entity.getRoutine().getId());
        dto.setHabitId(entity.getHabit().getId());
        dto.setOrderInRoutine(entity.getOrderInRoutine());
        dto.setTargetTimeInRoutine(entity.getTargetTimeInRoutine() != null ? entity.getTargetTimeInRoutine().toString() : null);
        dto.setNotes(entity.getNotes());
        return dto;
    }
}
