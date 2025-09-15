package com.tarea.services;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.dtos.HabitActivityDTO;
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

    public List<HabitActivityListDTO> getFavoriteHabitsListByUser(Long userId) {
        return favoriteHabitRepository.findByUser_Id(userId).stream()
            .map(fav -> {
                Habitactivity habit = fav.getHabit();
                HabitActivityListDTO dto = new HabitActivityListDTO();
                dto.setId(habit.getId());
                dto.setName(habit.getName());
                dto.setCategory(habit.getCategory());
                return dto;
            }).collect(Collectors.toList());
    }

    public List<HabitActivityListDTO> getFavoriteHabitsByCategory(Long userId, String category) {
        return favoriteHabitRepository.findByUser_IdAndHabit_Category(userId, category).stream()
            .map(fav -> {
                Habitactivity habit = fav.getHabit();
                HabitActivityListDTO dto = new HabitActivityListDTO();
                dto.setId(habit.getId());
                dto.setName(habit.getName());
                dto.setCategory(habit.getCategory());
                return dto;
            }).collect(Collectors.toList());
    }

    public HabitActivityDTO getFavoriteHabitDetailByName(Long userId, String name) {
        FavoriteHabit fav = favoriteHabitRepository.findByUser_IdAndHabit_Name(userId, name);
        if (fav == null) return null;
        Habitactivity habit = fav.getHabit();
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(habit.getId());
        dto.setName(habit.getName());
        dto.setCategory(habit.getCategory());
        dto.setDescription(habit.getDescription());
        dto.setDuration(habit.getDuration());
        dto.setTargetTime(habit.getTargetTime() != null ? habit.getTargetTime().toString() : null);
        dto.setNotes(habit.getNotes());
        return dto;
    }
}
