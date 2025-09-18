package com.tarea.services;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.dtos.RoutineHabitDetailDTO;
import com.tarea.models.Routine;
import com.tarea.models.RoutineHabit;
import com.tarea.models.User;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutineHabitRepository routineHabitRepository;

    public RoutineService(RoutineRepository routineRepository,
                          UserRepository userRepository,
                          RoutineHabitRepository routineHabitRepository) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
        this.routineHabitRepository = routineHabitRepository;
    }

    public List<RoutineDTO> getAll() {
        return routineRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RoutineDTO getById(Long id) {
        return routineRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<RoutineDTO> getByUserId(Long userId) {
        return routineRepository.findByUser_Id(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoutineDTO save(RoutineDTO dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("userId es obligatorio.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado: " + dto.getUserId()));

        final Routine entity;

        if (dto.getId() != null) {
            // UPDATE
            entity = routineRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Rutina no encontrada: " + dto.getId()));
        } else {
            // CREATE (¡NO setear id!)
            entity = new Routine();
        }

        entity.setTitle(dto.getTitle());
        entity.setDaysOfWeek(dto.getDaysOfWeek());
        entity.setUser(user);

        Routine saved = routineRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        routineRepository.deleteById(id);
    }

    private RoutineDTO toDTO(Routine entity) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDaysOfWeek(entity.getDaysOfWeek());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        return dto;
    }

    // Nuevo método:
    public RoutineDetailDTO getRoutineDetail(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + routineId));

        RoutineDetailDTO dto = new RoutineDetailDTO();
        dto.setId(routine.getId());
        dto.setTitle(routine.getTitle());
        dto.setDaysOfWeek(routine.getDaysOfWeek());

        List<RoutineHabit> routineHabits = routineHabitRepository.findByRoutine_IdOrderByOrderInRoutine(routineId);

        List<RoutineHabitDetailDTO> habits = routineHabits.stream().map(rh -> {
            RoutineHabitDetailDTO h = new RoutineHabitDetailDTO();
            h.setHabitId(rh.getHabit().getId());
            h.setName(rh.getHabit().getName());
            h.setCategory(rh.getHabit().getCategory());
            h.setDescription(rh.getHabit().getDescription());
            h.setOrderInRoutine(rh.getOrderInRoutine());
            h.setTargetTimeInRoutine(rh.getTargetTimeInRoutine() != null ? rh.getTargetTimeInRoutine().toString() : null);
            h.setNotes(rh.getNotes());
            return h;
        }).toList();

        dto.setHabits(habits);
        return dto;
    }
}
