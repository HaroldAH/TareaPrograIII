package com.tarea.services;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.models.Completedactivity;
import com.tarea.models.Habitactivity;
import com.tarea.models.Routine;
import com.tarea.models.User;
import com.repositories.CompletedActivityRepository;
import com.repositories.HabitActivityRepository;
import com.repositories.RoutineRepository;
import com.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompletedActivityService {

    private final CompletedActivityRepository completedActivityRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final HabitActivityRepository habitActivityRepository;

    public CompletedActivityService(
            CompletedActivityRepository completedActivityRepository,
            UserRepository userRepository,
            RoutineRepository routineRepository,
            HabitActivityRepository habitActivityRepository
    ) {
        this.completedActivityRepository = completedActivityRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.habitActivityRepository = habitActivityRepository;
    }

    public List<CompletedActivityDTO> getAll() {
        return completedActivityRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CompletedActivityDTO getById(Long id) {
        return completedActivityRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CompletedActivityDTO save(CompletedActivityDTO dto) {
        Completedactivity entity = new Completedactivity();
        entity.setId(dto.getId());

        Optional<User> user = userRepository.findById(dto.getUserId());
        Optional<Routine> routine = routineRepository.findById(dto.getRoutineId());
        Optional<Habitactivity> habit = habitActivityRepository.findById(dto.getHabitId());

        entity.setUser(user.orElse(null));
        entity.setRoutine(routine.orElse(null));
        entity.setHabit(habit.orElse(null));
        entity.setDate(dto.getDate() != null ? java.time.LocalDate.parse(dto.getDate()) : null);
        entity.setCompletedAt(dto.getCompletedAt());
        entity.setIsCompleted(dto.getIsCompleted());
        entity.setNotes(dto.getNotes());

        Completedactivity saved = completedActivityRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        completedActivityRepository.deleteById(id);
    }

    private CompletedActivityDTO toDTO(Completedactivity entity) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setRoutineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null);
        dto.setHabitId(entity.getHabit() != null ? entity.getHabit().getId() : null);
        dto.setDate(entity.getDate() != null ? entity.getDate().toString() : null);
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setIsCompleted(entity.getIsCompleted());
        dto.setNotes(entity.getNotes());
        return dto;
    }
}
