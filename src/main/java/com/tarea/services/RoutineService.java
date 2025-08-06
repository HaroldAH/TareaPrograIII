package com.tarea.services;

import com.tarea.dtos.RoutineDTO;
import com.tarea.models.Routine;
import com.tarea.models.User;
import com.repositories.RoutineRepository;
import com.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;

    public RoutineService(RoutineRepository routineRepository,
                          UserRepository userRepository) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
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

    public RoutineDTO save(RoutineDTO dto) {
        Routine entity = new Routine();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDaysOfWeek(dto.getDaysOfWeek());

        Optional<User> user = userRepository.findById(dto.getUserId());
        entity.setUser(user.orElse(null));

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
}
