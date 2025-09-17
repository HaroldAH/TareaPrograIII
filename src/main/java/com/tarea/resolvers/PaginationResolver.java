package com.tarea.resolvers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.tarea.dtos.*;
import com.tarea.models.Completedactivity;
import com.tarea.models.Guide;
import com.tarea.models.Habitactivity;
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.repositories.GuideRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.resolvers.inputs.PageRequestInput;

@Controller
public class PaginationResolver {

    private final HabitActivityRepository habitRepo;
    private final GuideRepository guideRepo;
    private final CompletedActivityRepository completedRepo;

    public PaginationResolver(HabitActivityRepository habitRepo,
                              GuideRepository guideRepo,
                              CompletedActivityRepository completedRepo) {
        this.habitRepo = habitRepo;
        this.guideRepo = guideRepo;
        this.completedRepo = completedRepo;
    }

    // ===== HabitActivity =====
    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public HabitActivityPageDTO habitActivitiesPage(@Argument PageRequestInput page) {
        Pageable pageable = page.toPageable();
        Page<Habitactivity> p = habitRepo.findAll(pageable);
        List<HabitActivityDTO> content = p.getContent().stream()
            .map(this::toHabitDTO)
            .collect(Collectors.toList());
        return new HabitActivityPageDTO(content, toPageInfo(p));
    }

    // ===== Guide =====
    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public GuidePageDTO guidesPage(@Argument PageRequestInput page) {
        Pageable pageable = page.toPageable();
        Page<Guide> p = guideRepo.findAll(pageable);
        List<GuideDTO> content = p.getContent().stream()
            .map(this::toGuideDTO)
            .collect(Collectors.toList());
        return new GuidePageDTO(content, toPageInfo(p));
    }

    // ===== CompletedActivity =====
    // Mantengo la misma política de acceso que tu getAllCompletedActivities
    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public CompletedActivityPageDTO completedActivitiesPage(@Argument PageRequestInput page) {
        Pageable pageable = page.toPageable();
        Page<Completedactivity> p = completedRepo.findAll(pageable);
        List<CompletedActivityDTO> content = p.getContent().stream()
            .map(this::toCompletedDTO)
            .collect(Collectors.toList());
        return new CompletedActivityPageDTO(content, toPageInfo(p));
    }

    // ===== Mappers =====
    private HabitActivityDTO toHabitDTO(Habitactivity h) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(h.getId());
        dto.setName(h.getName());
        dto.setCategory(h.getCategory());
        dto.setDescription(h.getDescription());
        dto.setDuration(h.getDuration());
        dto.setTargetTime(h.getTargetTime() == null ? null : h.getTargetTime().toString()); // "HH:mm:ss" -> GraphQL String
        dto.setNotes(h.getNotes());
        return dto;
    }

    private GuideDTO toGuideDTO(Guide g) {
        GuideDTO dto = new GuideDTO();
        dto.setId(g.getId());
        dto.setTitle(g.getTitle());
        dto.setContent(g.getContent());
        dto.setCategory(g.getCategory());
        dto.setUserId(g.getUser() == null ? null : g.getUser().getId());
        return dto;
    }

    private CompletedActivityDTO toCompletedDTO(Completedactivity c) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(c.getId());
        dto.setUserId(c.getUser() == null ? null : c.getUser().getId());
        dto.setRoutineId(c.getRoutine() == null ? null : c.getRoutine().getId());
        dto.setHabitId(c.getHabit() == null ? null : c.getHabit().getId());
        dto.setDate(c.getDate() == null ? null : c.getDate().toString());         // "YYYY-MM-DD"
        dto.setCompletedAt(c.getCompletedAt());                                    // ya es String en tu BD
        dto.setIsCompleted(c.getIsCompleted());
        dto.setNotes(c.getNotes());
        return dto;
    }

    private PageInfoDTO toPageInfo(Page<?> p) {
        return new PageInfoDTO(
            (int) p.getTotalElements(),
            p.getTotalPages(),
            p.getNumber(),
            p.getSize(),
            p.getNumberOfElements(),
            p.hasNext(),
            p.hasPrevious()
        );
    }
}
