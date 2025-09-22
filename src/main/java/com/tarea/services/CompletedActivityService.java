package com.tarea.services;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.models.Completedactivity;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;
import com.tarea.models.Routine;
import com.tarea.models.User;
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.SecurityUtils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tarea.security.SecurityUtils.*;

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

    /* ========== QUERIES ========== */

    // Global: requiere VIEW(PROGRESS)
    public List<CompletedActivityDTO> getAll() {
        requireView(Module.PROGRESS);
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Ver uno: dueño o VIEW
    public CompletedActivityDTO getById(Long id) {
        Completedactivity e = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CompletedActivity no encontrada: " + id));
        requireSelfOrView(e.getUser().getId(), Module.PROGRESS);
        return toDTO(e);
    }

    // Por usuario: dueño o VIEW
    public List<CompletedActivityDTO> getByUser(Long userId, String startDate, String endDate) {
        requireSelfOrView(userId, Module.PROGRESS);
        LocalDate start = parseDateOrNull(startDate);
        LocalDate end   = parseDateOrNull(endDate);

        List<Completedactivity> list;
        if (start != null && end != null) {
            list = repo.findByUser_IdAndDateBetween(userId, start, end);
        } else if (start != null) {
            list = repo.findByUser_IdAndDateGreaterThanEqual(userId, start);
        } else if (end != null) {
            list = repo.findByUser_IdAndDateLessThanEqual(userId, end);
        } else {
            list = repo.findByUser_Id(userId);
        }
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // “Mis …” (sin VIEW)
    public List<CompletedActivityDTO> getMine(String startDate, String endDate) {
        return getByUser(SecurityUtils.userId(), startDate, endDate);
    }
    public List<CompletedDayDTO> getMinePerDay(String startDate, String endDate) {
        return getCompletedByUserPerDay(SecurityUtils.userId(), startDate, endDate);
    }
    public List<CompletedWeekDTO> getMinePerWeek(String startDate, String endDate) {
        return getCompletedByUserPerWeek(SecurityUtils.userId(), startDate, endDate);
    }
    public CompletedDayDTO getMineOnDay(String date) {
        return getCompletedByUserOnDay(SecurityUtils.userId(), date);
    }

    // Agrupación por día: dueño o VIEW
    public List<CompletedDayDTO> getCompletedByUserPerDay(Long userId, String startDate, String endDate) {
        requireSelfOrView(userId, Module.PROGRESS);
        LocalDate start = parseDateOrNull(startDate);
        LocalDate end   = parseDateOrNull(endDate);

        List<Completedactivity> data =
            repo.findByUser_IdAndDateBetween(userId,
                start != null ? start : LocalDate.MIN,
                end   != null ? end   : LocalDate.MAX);

        Map<LocalDate, List<Completedactivity>> grouped = data.stream()
            .collect(Collectors.groupingBy(Completedactivity::getDate, LinkedHashMap::new, Collectors.toList()));

        List<CompletedDayDTO> out = new ArrayList<>();
        grouped.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> {
                CompletedDayDTO d = new CompletedDayDTO();
                d.setDate(e.getKey().toString());
                d.setActivities(e.getValue().stream().map(this::toDTO).collect(Collectors.toList()));
                d.setTotalCompleted(e.getValue().size());
                out.add(d);
            });
        return out;
    }

    // Agrupación por semana (ISO): dueño o VIEW
    public List<CompletedWeekDTO> getCompletedByUserPerWeek(Long userId, String startDate, String endDate) {
        requireSelfOrView(userId, Module.PROGRESS);
        LocalDate start = parseDateOrNull(startDate);
        LocalDate end   = parseDateOrNull(endDate);

        List<Completedactivity> data =
            repo.findByUser_IdAndDateBetween(userId,
                start != null ? start : LocalDate.MIN,
                end   != null ? end   : LocalDate.MAX);

        WeekFields wf = WeekFields.of(Locale.getDefault());
        record WKey(int year, int week) {}

        return data.stream()
            .collect(Collectors.groupingBy(a -> new WKey(
                a.getDate().get(wf.weekBasedYear()),
                a.getDate().get(wf.weekOfWeekBasedYear())
            )))
            .entrySet().stream()
            .sorted(Comparator.comparing((Map.Entry<WKey, List<Completedactivity>> e) -> e.getKey().year)
                .thenComparing(e -> e.getKey().week))
            .map(e -> {
                var dto = new CompletedWeekDTO();
                dto.setWeekLabel(e.getKey().year() + "-W" + e.getKey().week());
                dto.setTotalCompleted(e.getValue().size());
                dto.setRoutines(List.of());
                return dto;
            })
            .collect(Collectors.toList());
    }

    // Un día: dueño o VIEW
    public CompletedDayDTO getCompletedByUserOnDay(Long userId, String date) {
        requireSelfOrView(userId, Module.PROGRESS);
        LocalDate d = LocalDate.parse(date);
        List<Completedactivity> list = repo.findByUser_IdAndDateBetween(userId, d, d);
        var out = new CompletedDayDTO();
        out.setDate(date);
        out.setActivities(list.stream().map(this::toDTO).collect(Collectors.toList()));
        out.setTotalCompleted(list.size());
        return out;
    }

    /* ========== COMMANDS ========== */

    @Transactional
    public CompletedActivityDTO save(CompletedActivityDTO dto) {
        forbidAuditorWrites(); // auditor = solo lectura

        Long me = userId();
        Long target = (dto.getUserId() != null) ? dto.getUserId() : me;

        // Dueño puede crear/editar sin MUTATE; otros → MUTATE
        requireSelfOrMutate(target, Module.PROGRESS);

        User user = userRepo.findById(target)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + target));

        Habitactivity habit = null;
        if (dto.getHabitId() != null) {
            habit = habitRepo.findById(dto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado: " + dto.getHabitId()));
        }

        Routine routine = null;
        if (dto.getRoutineId() != null) {
            routine = routineRepo.findById(dto.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + dto.getRoutineId()));
        }

        Completedactivity e;
        if (dto.getId() != null) {
            e = repo.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("CompletedActivity no encontrada: " + dto.getId()));
            requireSelfOrMutate(e.getUser().getId(), Module.PROGRESS);
        } else {
            e = new Completedactivity();
            e.setUser(user);
        }

        e.setUser(user);
        e.setHabit(habit);
        e.setRoutine(routine);
        e.setDate(parseDateOrNull(dto.getDate())); // LocalDate
        e.setCompletedAt(dto.getCompletedAt());    // "HH:mm"
        e.setNotes(dto.getNotes());

        return toDTO(repo.save(e));
    }

    @Transactional
    public void delete(Long id) {
        forbidAuditorWrites();
        Completedactivity e = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CompletedActivity no encontrada: " + id));
        requireSelfOrMutate(e.getUser().getId(), Module.PROGRESS);
        repo.delete(e);
    }

    /* ========== STATS ========== */

    /**
     * Si month == null -> todo el año.
     * Si userId == null -> usa el del token (no permitimos global sin staff).
     * Si userId != me -> requiere VIEW(PROGRESS).
     */
    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(int year, Integer month, Long userIdArg) {
        Long me = userId();
        Long target = (userIdArg == null) ? me : userIdArg;
        if (!me.equals(target)) requireView(Module.PROGRESS);

        List<Object[]> rows = (month == null)
            ? repo.monthlyCategoryStatsYear(year, target)
            : repo.monthlyCategoryStatsMonth(year, month, target);

        return rows.stream().map(r -> mapMonthlyRow(r, month)).collect(Collectors.toList());
    }

    private MonthlyCategoryStatDTO mapMonthlyRow(Object[] r, Integer forcedMonth) {
        int y = ((Number) r[0]).intValue();
        int m = (forcedMonth != null) ? forcedMonth : ((Number) r[1]).intValue();
        String category = (String) r[2];
        int total = ((Number) r[3]).intValue();
        int uniqueDays = ((Number) r[4]).intValue();
        int totalDuration = ((Number) r[5]).intValue();
        int weeksActive = ((Number) r[6]).intValue();

        int daysInMonth = YearMonth.of(y, m).lengthOfMonth();
        float avgPerActiveDay = uniqueDays == 0 ? 0f : ((float) total) / uniqueDays;
        float activeDayRatio  = daysInMonth == 0 ? 0f : ((float) uniqueDays) / daysInMonth;
        float avgPerActiveWeek = weeksActive == 0 ? 0f : ((float) total) / weeksActive;

        MonthlyCategoryStatDTO dto = new MonthlyCategoryStatDTO();
        dto.setYear(y);
        dto.setMonth(m);
        dto.setCategory(category);
        dto.setTotalCompletions(total);
        dto.setUniqueDays(uniqueDays);
        dto.setAvgPerActiveDay(round2(avgPerActiveDay));
        dto.setTotalDurationMinutes(totalDuration);
        dto.setActiveDayRatio(round4(activeDayRatio));
        dto.setWeeksActive(weeksActive);
        dto.setAvgPerActiveWeek(round2(avgPerActiveWeek));
        return dto;
    }

    private float round2(float v){ return Math.round(v * 100f) / 100f; }
    private float round4(float v){ return Math.round(v * 10000f) / 10000f; }

    /* ========== helpers ========== */
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

    private LocalDate parseDateOrNull(String s) {
        return (s == null || s.isBlank()) ? null : LocalDate.parse(s);
    }
}
