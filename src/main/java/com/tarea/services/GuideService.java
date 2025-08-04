package com.tarea.services;

import com.tarea.dtos.GuideDTO;
import com.tarea.models.Guide;
import com.repositories.GuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideService {

    private final GuideRepository guideRepository;

    public GuideService(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    public List<GuideDTO> getAll() {
        return guideRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GuideDTO getById(Long id) {
        return guideRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public GuideDTO save(GuideDTO dto) {
        Guide entity = new Guide();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCategory(dto.getCategory());

        return toDTO(guideRepository.save(entity));
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
        return dto;
    }
}

