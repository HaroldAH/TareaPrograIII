package com.tarea.services;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.dtos.RoutineHabitDetailDTO;
import com.tarea.models.Module;
import com.tarea.models.Routine;
import com.tarea.models.RoutineHabit;
import com.tarea.models.User;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.InputSanitizationUtils;
import com.tarea.security.SecurityUtils;
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

    /** Global: sólo staff con VIEW en ROUTINES */
    public List<RoutineDTO> getAll() {
        SecurityUtils.requireView(Module.ROUTINES); // 🔐
        return routineRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Ver una rutina: dueño o VIEW en ROUTINES */
    public RoutineDTO getById(Long id) {
        return routineRepository.findById(id)
                .map(r -> {
                    Long owner = r.getUser() != null ? r.getUser().getId() : null;
                    if (owner != null) {
                        SecurityUtils.requireSelfOrView(owner, Module.ROUTINES); // 🔐
                    } else {
                        SecurityUtils.requireView(Module.ROUTINES); // 🔐 sin dueño: sólo staff
                    }
                    return toDTO(r);
                })
                .orElse(null);
    }

    /** Por usuario: dueño o VIEW */
    public List<RoutineDTO> getByUserId(Long userId) {
        SecurityUtils.requireSelfOrView(userId, Module.ROUTINES); // 🔐
        return routineRepository.findByUser_Id(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoutineDTO save(RoutineDTO dto) {
        InputSanitizationUtils.validateAllStringFields(dto);
        SecurityUtils.forbidAuditorWrites();                           // ⛔ auditor solo lectura

        Long me = SecurityUtils.userId();
        Long targetUserId = (dto.getUserId() != null) ? dto.getUserId() : me;

        // 🔐 self o MUTATE para tocar a otros
        SecurityUtils.requireSelfOrMutate(targetUserId, Module.ROUTINES);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + targetUserId));

        final Routine entity;

        if (dto.getId() != null) {
            // UPDATE
            entity = routineRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getId()));
            // 🔐 Dueño o MUTATE
            Long owner = entity.getUser() != null ? entity.getUser().getId() : null;
            if (owner != null) {
                SecurityUtils.requireSelfOrMutate(owner, Module.ROUTINES);
            } else {
                SecurityUtils.requireMutate(Module.ROUTINES);
            }
        } else {
            // CREATE
            entity = new Routine();
        }

        entity.setTitle(dto.getTitle());
        entity.setDaysOfWeek(dto.getDaysOfWeek());
        entity.setUser(user);

        Routine saved = routineRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        SecurityUtils.forbidAuditorWrites();                           // ⛔ auditor solo lectura

        Routine r = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + id));
        Long owner = r.getUser() != null ? r.getUser().getId() : null;

        // 🔐 Dueño o MUTATE
        if (owner != null) {
            SecurityUtils.requireSelfOrMutate(owner, Module.ROUTINES);
        } else {
            SecurityUtils.requireMutate(Module.ROUTINES);
        }
        routineRepository.delete(r);
    }

    private RoutineDTO toDTO(Routine entity) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDaysOfWeek(entity.getDaysOfWeek());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        return dto;
    }

    /** Detalle: dueño o VIEW */
    public RoutineDetailDTO getRoutineDetail(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + routineId));

        Long owner = routine.getUser() != null ? routine.getUser().getId() : null;
        if (owner != null) {
            SecurityUtils.requireSelfOrView(owner, Module.ROUTINES); // 🔐
        } else {
            SecurityUtils.requireView(Module.ROUTINES); // 🔐
        }

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
