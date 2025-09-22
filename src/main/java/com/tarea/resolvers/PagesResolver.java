package com.tarea.resolvers;

import com.tarea.dtos.*;
import com.tarea.models.*;
import com.tarea.repositories.*;
import com.tarea.resolvers.inputs.PageRequestInput;
import com.tarea.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.tarea.models.Module;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class PagesResolver {

    private final CompletedActivityRepository completedRepo;
    private final HabitActivityRepository habitRepo;
    private final GuideRepository guideRepo;
    private final RoutineRepository routineRepo;
    private final ReminderRepository reminderRepo;
    private final FavoriteHabitRepository favoriteRepo;
    private final RoutineHabitRepository routineHabitRepo;
    private final GuideHabitRepository guideHabitRepo;

    public PagesResolver(CompletedActivityRepository completedRepo,
                         HabitActivityRepository habitRepo,
                         GuideRepository guideRepo,
                         RoutineRepository routineRepo,
                         ReminderRepository reminderRepo,
                         FavoriteHabitRepository favoriteRepo,
                         RoutineHabitRepository routineHabitRepo,
                         GuideHabitRepository guideHabitRepo) {
        this.completedRepo = completedRepo;
        this.habitRepo = habitRepo;
        this.guideRepo = guideRepo;
        this.routineRepo = routineRepo;
        this.reminderRepo = reminderRepo;
        this.favoriteRepo = favoriteRepo;
        this.routineHabitRepo = routineHabitRepo;
        this.guideHabitRepo = guideHabitRepo;
    }

 

    private Pageable toPageable(PageRequestInput in) {
        return (in == null) ? PageRequest.of(0, 20) : in.toPageable();
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

 

    @QueryMapping
    public CompletedActivityPageDTO completedActivitiesPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.PROGRESS);
        Page<Completedactivity> p = completedRepo.findAll(toPageable(req));
        List<CompletedActivityDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new CompletedActivityPageDTO(content, toPageInfo(p));
    }

    private CompletedActivityDTO toDTO(Completedactivity e) {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setId(e.getId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setRoutineId(e.getRoutine() != null ? e.getRoutine().getId() : null);
        dto.setHabitId(e.getHabit() != null ? e.getHabit().getId() : null);
        dto.setDate(e.getDate() != null ? e.getDate().toString() : null);
        dto.setCompletedAt(e.getCompletedAt());
        dto.setNotes(e.getNotes());
        return dto;
    }

 

    @QueryMapping
    public HabitActivityPageDTO habitActivitiesPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.HABITS);
        Page<Habitactivity> p = habitRepo.findAll(toPageable(req));
        List<HabitActivityDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new HabitActivityPageDTO(content, toPageInfo(p));
    }

    private HabitActivityDTO toDTO(Habitactivity e) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setCategory(e.getCategory());
        dto.setDescription(e.getDescription());
        dto.setDuration(e.getDuration());
        dto.setTargetTime(e.getTargetTime() != null ? e.getTargetTime().toString() : null);
        dto.setNotes(e.getNotes());
        return dto;
    }

 

    @QueryMapping
    public GuidePageDTO guidesPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.GUIDES);
        Page<Guide> p = guideRepo.findAll(toPageable(req));
        List<GuideDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new GuidePageDTO(content, toPageInfo(p));
    }

    private GuideDTO toDTO(Guide g) {
        GuideDTO dto = new GuideDTO();
        dto.setId(g.getId());
        dto.setTitle(g.getTitle());
        dto.setContent(g.getContent());
        dto.setCategory(g.getCategory());
        dto.setUserId(g.getUser() != null ? g.getUser().getId() : null);
        return dto;
    }

 

    @QueryMapping
    public RoutinePageDTO routinesPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.ROUTINES);
        Page<Routine> p = routineRepo.findAll(toPageable(req));
        List<RoutineDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new RoutinePageDTO(content, toPageInfo(p));
    }

    private RoutineDTO toDTO(Routine r) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setUserId(r.getUser() != null ? r.getUser().getId() : null);
        dto.setDaysOfWeek(r.getDaysOfWeek());

        return dto;
    }

 

    @QueryMapping
    public ReminderPageDTO remindersPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.REMINDERS);
        Page<Reminder> p = reminderRepo.findAll(toPageable(req));
        List<ReminderDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new ReminderPageDTO(content, toPageInfo(p));
    }


    private ReminderDTO toDTO(Reminder e) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(e.getId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setHabitId(e.getHabit() != null ? e.getHabit().getId() : null);
        dto.setTime(e.getTime());
        dto.setFrequency(e.getFrequency());
        return dto;
    }

 

    @QueryMapping
    public FavoriteHabitPageDTO favoriteHabitsPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.HABITS);
        Page<FavoriteHabit> p = favoriteRepo.findAll(toPageable(req));
        List<FavoriteHabitDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new FavoriteHabitPageDTO(content, toPageInfo(p));
    }


    private FavoriteHabitDTO toDTO(FavoriteHabit e) {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setId(e.getId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setHabitId(e.getHabit() != null ? e.getHabit().getId() : null);
        return dto;
    }

 

    @QueryMapping
    public RoutineHabitPageDTO routineHabitsPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.ROUTINES);
        Page<RoutineHabit> p = routineHabitRepo.findAll(toPageable(req));
        List<RoutineHabitDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new RoutineHabitPageDTO(content, toPageInfo(p));
    }


    private RoutineHabitDTO toDTO(RoutineHabit e) {
        RoutineHabitDTO dto = new RoutineHabitDTO();
        dto.setRoutineId(e.getRoutine() != null ? e.getRoutine().getId() : null);
        dto.setHabitId(e.getHabit() != null ? e.getHabit().getId() : null);
        dto.setOrderInRoutine(e.getOrderInRoutine());
        dto.setTargetTimeInRoutine(e.getTargetTimeInRoutine() != null ? e.getTargetTimeInRoutine().toString() : null);
        dto.setNotes(e.getNotes());
        return dto;
    }

 

    @QueryMapping
    public GuideHabitPageDTO guideHabitsPage(@Argument("page") PageRequestInput req) {
        SecurityUtils.requireView(Module.GUIDES);
        Page<GuideHabit> p = guideHabitRepo.findAll(toPageable(req));
        List<GuideHabitDTO> content = p.getContent().stream().map(this::toDTO).toList();
        return new GuideHabitPageDTO(content, toPageInfo(p));
    }


    private GuideHabitDTO toDTO(GuideHabit e) {
        GuideHabitDTO dto = new GuideHabitDTO();
        dto.setGuideId(e.getGuide() != null ? e.getGuide().getId() : null);
        dto.setHabitId(e.getHabit() != null ? e.getHabit().getId() : null);
        return dto;
    }
}
