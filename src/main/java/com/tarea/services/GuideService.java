package com.tarea.services;

import com.tarea.dtos.GuideDTO;
import com.tarea.dtos.GuideDetailDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.Guide;
import com.tarea.models.GuideHabit;
import com.tarea.models.User;
import com.tarea.repositories.GuideHabitRepository;
import com.tarea.repositories.GuideRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.InputSanitizationUtils;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideService {
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final GuideHabitRepository guideHabitRepository;

    @Autowired
    public GuideService(GuideRepository guideRepository, UserRepository userRepository, GuideHabitRepository guideHabitRepository) {
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
        this.guideHabitRepository = guideHabitRepository;
    }

    public List<GuideDTO> getAll() {
        return guideRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GuideDTO getById(Long id) {
        return guideRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<GuideDTO> getByCategory(String category) {
        return guideRepository.findByCategory(category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GuideDTO> getByUserId(Long userId) {
        return guideRepository.findByUser_Id(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GuideDTO> getByCategoryAndUserId(String category, Long userId) {
        return guideRepository.findByCategoryAndUser_Id(category, userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GuideDTO save(GuideDTO dto) {
        InputSanitizationUtils.validateAllStringFields(dto);
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("userId es obligatorio.");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));

        Guide entity;
        if (dto.getId() != null) {
            entity = guideRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Guía no encontrada: " + dto.getId()));
        } else {
            entity = new Guide();
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCategory(dto.getCategory());
        entity.setUser(user);

        Guide saved = guideRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        guideRepository.deleteById(id);
    }

    private GuideDTO toDTO(Guide entity) {
        GuideDTO dto = new GuideDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCategory(entity.getCategory());
        dto.setUserId(entity.getUser().getId());
        return dto;
    }

    public GuideDetailDTO getGuideDetail(Long guideId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("Guía no encontrada: " + guideId));

        GuideDetailDTO dto = new GuideDetailDTO();
        dto.setId(guide.getId());
        dto.setTitle(guide.getTitle());
        dto.setContent(guide.getContent());
        dto.setCategory(guide.getCategory());
        dto.setUserId(guide.getUser().getId());

        List<GuideHabit> guideHabits = guideHabitRepository.findAll().stream()
                .filter(gh -> gh.getGuide().getId().equals(guideId))
                .toList();

        List<HabitActivityListDTO> recommendHabit = guideHabits.stream().map(gh -> {
            HabitActivityListDTO h = new HabitActivityListDTO();
            h.setId(gh.getHabit().getId());
            h.setName(gh.getHabit().getName());
            h.setCategory(gh.getHabit().getCategory());
            return h;
        }).toList();

        dto.setRecommendHabit(recommendHabit);
        return dto;
    }
}
