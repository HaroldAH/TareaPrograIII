package com.tarea.services;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.models.FavoriteHabit;
import com.tarea.models.User;
import com.tarea.models.Habitactivity;
import com.tarea.repositories.FavoriteHabitRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.repositories.HabitActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteHabitService {

    private final FavoriteHabitRepository favoriteHabitRepository;
    private final UserRepository userRepository;
    private final HabitActivityRepository habitActivityRepository;

    public FavoriteHabitService(FavoriteHabitRepository favoriteHabitRepository,
                               UserRepository userRepository,
                               HabitActivityRepository habitActivityRepository) {
        this.favoriteHabitRepository = favoriteHabitRepository;
        this.userRepository = userRepository;
        this.habitActivityRepository = habitActivityRepository;
    }

    public List<FavoriteHabitDTO> getAll() {
        return favoriteHabitRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FavoriteHabitDTO getById(Long id) {
        return favoriteHabitRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public FavoriteHabitDTO save(FavoriteHabitDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));
        Habitactivity habit = habitActivityRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

        FavoriteHabit entity;
        if (dto.getId() != null) {
            entity = favoriteHabitRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("FavoriteHabit no encontrada: " + dto.getId()));
        } else {
            entity = new FavoriteHabit();
        }
        entity.setUser(user);
        entity.setHabit(habit);

        FavoriteHabit saved = favoriteHabitRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        favoriteHabitRepository.deleteById(id);
    }

    private FavoriteHabitDTO toDTO(FavoriteHabit entity) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setHabitId(entity.getHabit().getId());
        return dto;
    }
}
