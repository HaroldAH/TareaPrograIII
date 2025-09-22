package com.tarea.services;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;                 // 👈
import com.tarea.models.Routine;              // 👈
import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.security.SecurityUtils;      // 👈
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

    /** Consulta: dueño de la rutina o VIEW en ROUTINES */
    public List<RoutineHabitDTO> getByRoutineId(Long routineId) {
        Routine r = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + routineId));
        Long owner = r.getUser().getId();
        SecurityUtils.requireSelfOrView(owner, Module.ROUTINES);   // 🔐

        return routineHabitRepository.findByRoutine_Id(routineId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Crear/actualizar: dueño de la rutina o MUTATE en ROUTINES */
    @Transactional
    public RoutineHabitDTO save(RoutineHabitDTO dto) {
        SecurityUtils.forbidAuditorWrites();                       // ⛔ auditor solo lectura

        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        Long owner = routine.getUser().getId();
        SecurityUtils.requireSelfOrMutate(owner, Module.ROUTINES); // 🔐

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

    /** Eliminar: dueño de la rutina o MUTATE en ROUTINES */
    public void delete(Long routineId, Long habitId) {
        SecurityUtils.forbidAuditorWrites();                       // ⛔ auditor solo lectura

        RoutineHabitId id = new RoutineHabitId();
        id.setRoutineId(routineId);
        id.setHabitId(habitId);

        RoutineHabit rh = routineHabitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo rutina-hábito no encontrado"));
        Long owner = rh.getRoutine().getUser().getId();
        SecurityUtils.requireSelfOrMutate(owner, Module.ROUTINES); // 🔐

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
