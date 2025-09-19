package com.tarea.resolvers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.tarea.models.FavoriteHabit;
import com.tarea.repositories.FavoriteHabitRepository;
import com.tarea.dtos.*;
import com.tarea.models.Completedactivity;
import com.tarea.models.Guide;
import com.tarea.models.Habitactivity;
import com.tarea.models.User; // <-- importa tu entidad User
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.repositories.GuideRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.UserRepository;          // <-- nuevo
import com.tarea.resolvers.inputs.PageRequestInput;
import com.tarea.models.Role;
import com.tarea.repositories.RoleRepository;
import com.tarea.models.Routine;
import com.tarea.repositories.RoutineRepository;
import com.tarea.models.Reminder;
import com.tarea.repositories.ReminderRepository;
import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import com.tarea.models.GuideHabit;
import com.tarea.models.GuideHabitId;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.GuideHabitRepository;



@Controller
public class PaginationResolver {
private final RoleRepository roleRepo; 
    private final HabitActivityRepository habitRepo;
    private final GuideRepository guideRepo;
    private final CompletedActivityRepository completedRepo;
    private final UserRepository userRepo;             // <-- nuevo
private final ReminderRepository reminderRepo; // NUEVO
private final FavoriteHabitRepository favoriteHabitRepo; // NUEVO
private final RoutineHabitRepository routineHabitRepo;   // NUEVO
private final GuideHabitRepository guideHabitRepo;       // NUEVO




   // campo:
private final RoutineRepository routineRepo;  // <-- ESTE

// constructor: AÑADE el parámetro y la asignación
public PaginationResolver(HabitActivityRepository habitRepo,
                          GuideRepository guideRepo,
                          CompletedActivityRepository completedRepo,
                          UserRepository userRepo,          // si ya lo añadiste
                          RoleRepository roleRepo,          // si ya lo añadiste
                          RoutineRepository routineRepo,
                          ReminderRepository reminderRepo,
                          FavoriteHabitRepository favoriteHabitRepo,
                           RoutineHabitRepository routineHabitRepo,
                          GuideHabitRepository guideHabitRepo) {
    this.habitRepo = habitRepo;
    this.guideRepo = guideRepo;
    this.completedRepo = completedRepo;
    this.userRepo = userRepo;        // si lo tienes
    this.roleRepo = roleRepo;        // si lo tienes
    this.routineRepo = routineRepo;  
    this.reminderRepo = reminderRepo;
    this.favoriteHabitRepo = favoriteHabitRepo;
    this.routineHabitRepo = routineHabitRepo;
    this.guideHabitRepo = guideHabitRepo;
// <-- Y ESTA LÍNEA
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

    // ===== Users (nuevo) =====
    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public UserPageDTO usersPage(@Argument PageRequestInput page) {
        Pageable pageable = page.toPageable();
        Page<User> p = userRepo.findAll(pageable);
        List<UserDTO> content = p.getContent().stream()
            .map(this::toUserDTO)
            .collect(Collectors.toList());
        return new UserPageDTO(content, toPageInfo(p));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public int countUsers() {
        return (int) userRepo.count();
    }


@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public RolePageDTO rolesPage(@Argument PageRequestInput page) {
    Pageable pageable = page.toPageable();
    Page<Role> p = roleRepo.findAll(pageable);
    List<RoleDTO> content = p.getContent().stream()
        .map(this::toRoleDTO)
        .collect(Collectors.toList());
    return new RolePageDTO(content, toPageInfo(p));
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public int countRoles() {
    return (int) roleRepo.count();
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public ReminderPageDTO remindersPage(@Argument PageRequestInput page) {
    Pageable pageable = page.toPageable();
    Page<Reminder> p = reminderRepo.findAll(pageable);
    List<ReminderDTO> content = p.getContent().stream()
        .map(r -> toReminderDTO(r))
        .collect(Collectors.toList());
    return new ReminderPageDTO(content, toPageInfo(p));
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public int countReminders() {
    return (int) reminderRepo.count();
}


@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public FavoriteHabitPageDTO favoriteHabitsPage(@Argument PageRequestInput page) {
    Pageable pageable = page.toPageable();
    Page<FavoriteHabit> p = favoriteHabitRepo.findAll(pageable);
    List<FavoriteHabitDTO> content = p.getContent().stream()
        .map(f -> toFavoriteHabitDTO(f))
        .collect(Collectors.toList());
    return new FavoriteHabitPageDTO(content, toPageInfo(p));
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public int countFavoriteHabits() {
    return (int) favoriteHabitRepo.count();
}
  // ===== Mappers =====
private FavoriteHabitDTO toFavoriteHabitDTO(FavoriteHabit f) {
    FavoriteHabitDTO dto = new FavoriteHabitDTO();
    dto.setId(f.getId());
    dto.setUserId(f.getUser() == null ? null : f.getUser().getId());
    dto.setHabitId(f.getHabit() == null ? null : f.getHabit().getId());
    return dto;
}

  
private ReminderDTO toReminderDTO(Reminder r) {
    ReminderDTO dto = new ReminderDTO();
    dto.setId(r.getId());
    dto.setUserId(r.getUser() == null ? null : r.getUser().getId());
    dto.setHabitId(r.getHabit() == null ? null : r.getHabit().getId());
    dto.setTime(r.getTime());
    dto.setFrequency(r.getFrequency());
    return dto;
}


    private HabitActivityDTO toHabitDTO(Habitactivity h) {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setId(h.getId());
        dto.setName(h.getName());
        dto.setCategory(h.getCategory());
        dto.setDescription(h.getDescription());
        dto.setDuration(h.getDuration());
        dto.setTargetTime(h.getTargetTime() == null ? null : h.getTargetTime().toString());
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
        dto.setDate(c.getDate() == null ? null : c.getDate().toString());
        dto.setCompletedAt(c.getCompletedAt());
        dto.setNotes(c.getNotes());
        return dto;
    }

    private UserDTO toUserDTO(User u) {                  
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
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


    private RoleDTO toRoleDTO(Role r) {
    RoleDTO dto = new RoleDTO();
    dto.setId(r.getId());
    dto.setName(r.getName());
    dto.setPermissions(r.getPermissions());
    return dto;
}



@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public RoutinePageDTO routinesPage(@Argument PageRequestInput page) {
    Pageable pageable = page.toPageable();
    Page<Routine> p = routineRepo.findAll(pageable);
    List<RoutineDTO> content = p.getContent().stream()
        .map(r -> toRoutineDTO(r))        // usa lambda si el IDE se queja del method-ref
        .collect(Collectors.toList());
    return new RoutinePageDTO(content, toPageInfo(p));
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public int countRoutines() {
    return (int) routineRepo.count();
}

private RoutineDTO toRoutineDTO(Routine r) {
    RoutineDTO dto = new RoutineDTO();
    dto.setId(r.getId());
    dto.setTitle(r.getTitle());
    dto.setUserId(r.getUser() == null ? null : r.getUser().getId());
    dto.setDaysOfWeek(r.getDaysOfWeek());
    return dto;
}
// ===== RoutineHabit =====
@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public RoutineHabitPageDTO routineHabitsPage(@Argument PageRequestInput page) {
    Pageable pageable = page.toPageable();
    Page<RoutineHabit> p = routineHabitRepo.findAll(pageable);
    List<RoutineHabitDTO> content = p.getContent().stream()
        .map(rh -> toRoutineHabitDTO(rh))
        .collect(Collectors.toList());
    return new RoutineHabitPageDTO(content, toPageInfo(p));
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public int countRoutineHabits() {
    return (int) routineHabitRepo.count();
}

private RoutineHabitDTO toRoutineHabitDTO(RoutineHabit rh) {
    RoutineHabitDTO dto = new RoutineHabitDTO();
    // Evitamos lazy: usamos el EmbeddedId
    dto.setRoutineId(rh.getId() == null ? null : rh.getId().getRoutineId());
    dto.setHabitId(rh.getId() == null ? null : rh.getId().getHabitId());
    dto.setOrderInRoutine(rh.getOrderInRoutine());
    dto.setTargetTimeInRoutine(rh.getTargetTimeInRoutine() == null ? null : rh.getTargetTimeInRoutine().toString());
    dto.setNotes(rh.getNotes());
    return dto;
}

// ===== GuideHabit =====
@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public GuideHabitPageDTO guideHabitsPage(@Argument PageRequestInput page) {
    Pageable pageable = page.toPageable();
    Page<GuideHabit> p = guideHabitRepo.findAll(pageable);
    List<GuideHabitDTO> content = p.getContent().stream()
        .map(gh -> toGuideHabitDTO(gh))
        .collect(Collectors.toList());
    return new GuideHabitPageDTO(content, toPageInfo(p));
}

@PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
@QueryMapping
public int countGuideHabits() {
    return (int) guideHabitRepo.count();
}

private GuideHabitDTO toGuideHabitDTO(GuideHabit gh) {
    GuideHabitDTO dto = new GuideHabitDTO();
    dto.setGuideId(gh.getId() == null ? null : gh.getId().getGuideId());
    dto.setHabitId(gh.getId() == null ? null : gh.getId().getHabitId());
    return dto;
}


}
