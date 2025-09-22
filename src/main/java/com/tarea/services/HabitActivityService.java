package com.tarea.services;

import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.Habitactivity;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.security.InputSanitizationUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
// com/tarea/services/HabitActivityService.java

public class HabitActivityService {

    private final HabitActivityRepository habitActivityRepository;

    public HabitActivityService(HabitActivityRepository habitActivityRepository) {
        this.habitActivityRepository = habitActivityRepository;
    }

    public List<HabitActivityDTO> getAll() {
        return habitActivityRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public HabitActivityDTO getById(Long id) {
        return habitActivityRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public HabitActivityDTO save(HabitActivityDTO dto) {
        InputSanitizationUtils.validateAllStringFields(dto);
        // Validación de seguridad en descripción
        if (InputSanitizationUtils.containsMaliciousPattern(dto.getDescription())) {
            throw new IllegalArgumentException("Malicious input detected in description");
        }

        final Habitactivity entity;

        if (dto.getId() != null) { // UPDATE
            entity = habitActivityRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "HabitActivity no encontrada: " + dto.getId()));
        } else {                    // CREATE (no seteamos id)
            entity = new Habitactivity();
        }

        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setDuration(dto.getDuration());
        entity.setTargetTime(dto.getTargetTime() != null && !dto.getTargetTime().isBlank()
                ? java.time.LocalTime.parse(dto.getTargetTime())  // "HH:mm"
                : null);
        entity.setNotes(dto.getNotes());

        Habitactivity saved = habitActivityRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        habitActivityRepository.deleteById(id);
    }

    public List<HabitActivityListDTO> getByCategory(String category) {
        return habitActivityRepository.findByCategory(category).stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    public HabitActivityDTO getByName(String name) {
        return habitActivityRepository.findByName(name)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<HabitActivityListDTO> getAllAsList() {
        return habitActivityRepository.findAll().stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    // Eliminados según sugerencia:
    // public List<HabitActivityDTO> getByRoutineId(Long routineId) { ... }
    // public List<HabitActivityListDTO> getByRoutineIdList(Long routineId) { ... }

    private HabitActivityDTO toDTO(Habitactivity entity) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setDescription(entity.getDescription());
        dto.setDuration(entity.getDuration());
        dto.setTargetTime(entity.getTargetTime() != null ? entity.getTargetTime().toString() : null);
        dto.setNotes(entity.getNotes());
        // dto.setRoutineId(entity.getRoutineId()); // Eliminado según sugerencia
        return dto;
    }

    private HabitActivityListDTO toListDTO(Habitactivity entity) {
        HabitActivityListDTO dto = new HabitActivityListDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        // dto.setRoutineId(entity.getRoutineId()); // Eliminado según sugerencia
        return dto;
    }
}
