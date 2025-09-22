package com.tarea.services;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.dtos.HabitActivityDTO;
import com.tarea.models.FavoriteHabit;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;
import com.tarea.models.User;
import com.tarea.repositories.FavoriteHabitRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.security.SecurityUtils;
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

    /** Lista global: sólo staff con VIEW en HABITS (o módulo propio si lo tienes) */
    public List<FavoriteHabitDTO> getAll() {
        SecurityUtils.requireView(Module.HABITS); // 🔐
        return favoriteHabitRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Ver uno: dueño o VIEW */
    public FavoriteHabitDTO getById(Long id) {
        return favoriteHabitRepository.findById(id)
                .map(fh -> {
                    SecurityUtils.requireSelfOrView(fh.getUser().getId(), Module.HABITS); // 🔐
                    return toDTO(fh);
                })
                .orElse(null);
    }

    @Transactional
    public FavoriteHabitDTO save(FavoriteHabitDTO dto) {
        SecurityUtils.forbidAuditorWrites();                         // ⛔ auditor solo lectura

        Long me = SecurityUtils.userId();
        Long target = (dto.getUserId() != null) ? dto.getUserId() : me;

        // 🔐 Self o MUTATE en HABITS para tocar a otros
        SecurityUtils.requireSelfOrMutate(target, Module.HABITS);

        User user = userRepository.findById(target)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + target));
        Habitactivity habit = habitActivityRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));

        FavoriteHabit entity;
        if (dto.getId() != null) {
            entity = favoriteHabitRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("FavoriteHabit no encontrada: " + dto.getId()));
            SecurityUtils.requireSelfOrMutate(entity.getUser().getId(), Module.HABITS); // 🔐
        } else {
            entity = new FavoriteHabit();
        }
        entity.setUser(user);
        entity.setHabit(habit);

        FavoriteHabit saved = favoriteHabitRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        SecurityUtils.forbidAuditorWrites();                         // ⛔ auditor solo lectura

        FavoriteHabit fh = favoriteHabitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FavoriteHabit no encontrada: " + id));
        // 🔐 Dueño o MUTATE
        SecurityUtils.requireSelfOrMutate(fh.getUser().getId(), Module.HABITS);
        favoriteHabitRepository.delete(fh);
    }

    private FavoriteHabitDTO toDTO(FavoriteHabit entity) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setHabitId(entity.getHabit().getId());
        return dto;
    }

    /** Mis favoritos (lista/card): dueño o VIEW para ver otros */
    public List<HabitActivityListDTO> getFavoriteHabitsListByUser(Long userId) {
        SecurityUtils.requireSelfOrView(userId, Module.HABITS); // 🔐
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

    /** Mis favoritos por categoría: dueño o VIEW para ver otros */
    public List<HabitActivityListDTO> getFavoriteHabitsByCategory(Long userId, String category) {
        SecurityUtils.requireSelfOrView(userId, Module.HABITS); // 🔐
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

    /** Detalle por nombre: dueño o VIEW para ver otros */
    public HabitActivityDTO getFavoriteHabitDetailByName(Long userId, String name) {
        SecurityUtils.requireSelfOrView(userId, Module.HABITS); // 🔐
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
