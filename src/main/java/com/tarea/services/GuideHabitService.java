package com.tarea.services;

import com.tarea.dtos.GuideHabitDTO;
import com.tarea.models.Guide;
import com.tarea.models.GuideHabit;
import com.tarea.models.Habitactivity;
import com.tarea.models.GuideHabitId;
import com.repositories.GuideHabitRepository;
import com.repositories.GuideRepository;
import com.repositories.HabitActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuideHabitService {

    private final GuideHabitRepository guideHabitRepository;
    private final GuideRepository guideRepository;
    private final HabitActivityRepository habitActivityRepository;

    public GuideHabitService(GuideHabitRepository guideHabitRepository,
                             GuideRepository guideRepository,
                             HabitActivityRepository habitActivityRepository) {
        this.guideHabitRepository = guideHabitRepository;
        this.guideRepository = guideRepository;
        this.habitActivityRepository = habitActivityRepository;
    }

    public List<GuideHabitDTO> getAll() {
        return guideHabitRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GuideHabitDTO getById(Long guideId, Long habitId) {
        GuideHabitId id = new GuideHabitId(guideId, habitId);
        return guideHabitRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public GuideHabitDTO save(GuideHabitDTO dto) {
        GuideHabit entity = new GuideHabit();
        GuideHabitId id = new GuideHabitId(dto.getGuideId(), dto.getHabitId());
        entity.setId(id);

        Optional<Guide> guide = guideRepository.findById(dto.getGuideId());
        Optional<Habitactivity> habit = habitActivityRepository.findById(dto.getHabitId());

        entity.setGuide(guide.orElse(null));
        entity.setHabit(habit.orElse(null));

        GuideHabit saved = guideHabitRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long guideId, Long habitId) {
        GuideHabitId id = new GuideHabitId(guideId, habitId);
        guideHabitRepository.deleteById(id);
    }

    private GuideHabitDTO toDTO(GuideHabit entity) {
        GuideHabitDTO dto = new GuideHabitDTO();
        dto.setGuideId(entity.getGuide() != null ? entity.getGuide().getId() : null);
        dto.setHabitId(entity.getHabit() != null ? entity.getHabit().getId() : null);
        return dto;
    }
}
