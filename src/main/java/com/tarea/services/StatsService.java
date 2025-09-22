package com.tarea.services;

import static com.tarea.security.SecurityUtils.requireView;
import static com.tarea.security.SecurityUtils.userId;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.models.Module;
import com.tarea.repositories.CompletedActivityRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final CompletedActivityRepository completedRepo;

    public StatsService(CompletedActivityRepository completedRepo) {
        this.completedRepo = completedRepo;
    }

    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(int year, Integer month, Long userIdArg) {
        Long me = userId();
        Long target;

        if (userIdArg == null) {
            if (canViewProgress()) {
                target = null;  
            } else {
                target = me;    
            }
        } else {
            if (!userIdArg.equals(me)) {
                requireView(Module.PROGRESS);  
            }
            target = userIdArg;  
        }

        List<Object[]> rows = (month == null)
            ? completedRepo.monthlyCategoryStatsYear(year, target)
            : completedRepo.monthlyCategoryStatsMonth(year, month, target);

        return rows.stream()
            .map(r -> mapRow(r, month))
            .collect(Collectors.toList());
    }

    private boolean canViewProgress() {
        try {
            requireView(Module.PROGRESS);
            return true;
        } catch (AccessDeniedException ex) {
            return false;
        }
    }

    private MonthlyCategoryStatDTO mapRow(Object[] r, Integer forcedMonth) {
        int y = ((Number) r[0]).intValue();
        int m = (forcedMonth != null) ? forcedMonth : ((Number) r[1]).intValue();
        String category = (String) r[2];
        int total = ((Number) r[3]).intValue();
        int uniqueDays = ((Number) r[4]).intValue();
        int totalDuration = ((Number) r[5]).intValue();
        int weeksActive = ((Number) r[6]).intValue();

        int daysInMonth = YearMonth.of(y, m).lengthOfMonth();
        float avgPerActiveDay = uniqueDays == 0 ? 0f : (float) total / uniqueDays;
        float activeDayRatio  = daysInMonth == 0 ? 0f : (float) uniqueDays / daysInMonth;
        float avgPerActiveWeek = weeksActive == 0 ? 0f : (float) total / weeksActive;

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
}
