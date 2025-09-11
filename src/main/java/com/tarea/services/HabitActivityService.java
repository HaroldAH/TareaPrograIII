package com.tarea.services;

import com.tarea.dtos.HabitActivityDTO;
import com.tarea.models.Habitactivity;
import com.tarea.repositories.HabitActivityRepository;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
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

    public HabitActivityDTO save(HabitActivityDTO dto) {
        final Habitactivity entity;

        if (dto.getId() != null) {
            // UPDATE
            entity = habitActivityRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Habit no encontrado: " + dto.getId()));
        } else {
            // CREATE
            entity = new Habitactivity();
        }

        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setDuration(dto.getDuration());
        entity.setTargetTime(dto.getTargetTime() != null ? LocalTime.parse(dto.getTargetTime()) : null);
        entity.setNotes(dto.getNotes());
        entity.setIsFavorite(dto.getIsFavorite());

        Habitactivity saved = habitActivityRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        habitActivityRepository.deleteById(id);
    }

    private HabitActivityDTO toDTO(Habitactivity entity) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setDescription(entity.getDescription());
        dto.setDuration(entity.getDuration());
        dto.setTargetTime(entity.getTargetTime() != null ? entity.getTargetTime().toString() : null);
        dto.setNotes(entity.getNotes());
        dto.setIsFavorite(entity.getIsFavorite());
        return dto;
    }
}
