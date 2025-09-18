// com/tarea/services/CompletedActivityService.java
package com.tarea.services;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedRoutineInWeekDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.models.Completedactivity;
import com.tarea.models.Habitactivity;
import com.tarea.models.Routine;
import com.tarea.models.User;
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompletedActivityService {

    private final CompletedActivityRepository repo;
    private final UserRepository userRepo;
    private final RoutineRepository routineRepo;
    private final HabitActivityRepository habitRepo;

    public CompletedActivityService(CompletedActivityRepository repo,
                                    UserRepository userRepo,
                                    RoutineRepository routineRepo,
                                    HabitActivityRepository habitRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.routineRepo = routineRepo;
        this.habitRepo = habitRepo;
    }

    public List<CompletedActivityDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CompletedActivityDTO getById(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public List<CompletedActivityDTO> getByUser(Long userId, String start, String end) {
        if (start != null && end != null) {
            LocalDate s = LocalDate.parse(start);
            LocalDate e = LocalDate.parse(end);
            return repo.findByUser_IdAndDateBetween(userId, s, e).stream().map(this::toDTO).collect(Collectors.toList());
        }
        return repo.findByUser_Id(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CompletedActivityDTO save(CompletedActivityDTO dto) {
        if (dto.getUserId() == null) throw new IllegalArgumentException("userId es obligatorio.");
        if (dto.getHabitId() == null && dto.getRoutineId() == null) {
            throw new IllegalArgumentException("Debe indicar habitId o routineId.");
        }

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUserId()));

        Routine routine = null;
        if (dto.getRoutineId() != null) {
            routine = routineRepo.findById(dto.getRoutineId())
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        }

        Habitactivity habit = null;
        if (dto.getHabitId() != null) {
            habit = habitRepo.findById(dto.getHabitId())
                    .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));
        }

        final Completedactivity entity;
        if (dto.getId() != null) {
            entity = repo.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado: " + dto.getId()));
        } else {
            entity = new Completedactivity();
        }

        entity.setUser(user);
        entity.setRoutine(routine);
        entity.setHabit(habit);
        entity.setDate(dto.getDate() != null ? LocalDate.parse(dto.getDate()) : null);
        entity.setCompletedAt(dto.getCompletedAt());
        entity.setNotes(dto.getNotes());

        return toDTO(repo.save(entity));
    }

    public void delete(Long id) {
        repo.deleteById(id);
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

    public List<CompletedDayDTO> getCompletedByUserPerDay(Long userId, String start, String end) {
        List<CompletedActivityDTO> all = getByUser(userId, start, end);
        Map<String, List<CompletedActivityDTO>> grouped = all.stream()
                .collect(Collectors.groupingBy(CompletedActivityDTO::getDate));
        return grouped.entrySet().stream()
                .map(e -> {
                    CompletedDayDTO dto = new CompletedDayDTO();
                    dto.setDate(e.getKey());
                    dto.setActivities(e.getValue());
                    dto.setTotalCompleted(e.getValue().size());
                    return dto;
                })
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // más reciente primero
                .collect(Collectors.toList());
    }

    public List<CompletedWeekDTO> getCompletedByUserPerWeek(Long userId, String start, String end) {
        List<CompletedActivityDTO> all = getByUser(userId, start, end);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<String, List<CompletedActivityDTO>> byWeek = all.stream()
                .collect(Collectors.groupingBy(dto -> {
                    LocalDate d = LocalDate.parse(dto.getDate());
                    int week = d.get(weekFields.weekOfWeekBasedYear());
                    int year = d.getYear();
                    return String.format("%d-W%02d", year, week);
                }));

        return byWeek.entrySet().stream()
                .map(e -> {
                    // Agrupa por rutina
                    Map<Long, List<CompletedActivityDTO>> byRoutine = e.getValue().stream()
                            .filter(dto -> dto.getRoutineId() != null)
                            .collect(Collectors.groupingBy(CompletedActivityDTO::getRoutineId));
                    List<CompletedRoutineInWeekDTO> routines = byRoutine.entrySet().stream()
                            .map(r -> {
                                CompletedRoutineInWeekDTO cr = new CompletedRoutineInWeekDTO();
                                cr.setRoutineId(r.getKey());
                                cr.setRoutineTitle(""); // Puedes setear el título si lo necesitas (requiere repo)
                                cr.setCompletedHabits(r.getValue());
                                return cr;
                            }).collect(Collectors.toList());
                    CompletedWeekDTO dto = new CompletedWeekDTO();
                    dto.setWeekLabel(e.getKey());
                    dto.setRoutines(routines);
                    dto.setTotalCompleted(e.getValue().size());
                    return dto;
                })
                .sorted((a, b) -> b.getWeekLabel().compareTo(a.getWeekLabel()))
                .collect(Collectors.toList());
    }

    public CompletedDayDTO getCompletedByUserOnDay(Long userId, String date) {
        List<CompletedActivityDTO> all = getByUser(userId, date, date);
        CompletedDayDTO dto = new CompletedDayDTO();
        dto.setDate(date);
        dto.setActivities(all);
        dto.setTotalCompleted(all.size());
        return dto;
    }
}
