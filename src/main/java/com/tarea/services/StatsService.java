package com.tarea.services;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.repositories.CompletedActivityRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatsService {

    private final CompletedActivityRepository completedActivityRepository;

    public StatsService(CompletedActivityRepository completedActivityRepository) {
        this.completedActivityRepository = completedActivityRepository;
    }

    /**
     * Si month == null -> trae todo el año.
     * Si month != null -> filtra por ese mes.
     */
    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(int year, Integer month, Long userId) {
        List<Object[]> rows = (month == null)
                ? completedActivityRepository.monthlyCategoryStatsYear(year, userId)
                : completedActivityRepository.monthlyCategoryStatsMonth(year, month, userId);

        List<MonthlyCategoryStatDTO> out = new ArrayList<>();

        for (Object[] r : rows) {
            int y = ((Number) r[0]).intValue();   // YEAR(ca.date)
            int m = ((Number) r[1]).intValue();   // MONTH(ca.date)
            String category = (String) r[2];

            int totalCompletions  = ((Number) r[3]).intValue(); // COUNT(*)
            int uniqueDays        = ((Number) r[4]).intValue(); // COUNT(DISTINCT ca.date)
            int totalDurationMins = ((Number) r[5]).intValue(); // SUM(ha.duration)
            int weeksActive       = ((Number) r[6]).intValue(); // COUNT(DISTINCT YEARWEEK(...))

            int daysInMonth = YearMonth.of(y, m).lengthOfMonth();

            double avgPerActiveDay  = uniqueDays == 0 ? 0.0 : (double) totalCompletions / uniqueDays;
            double activeDayRatio   = daysInMonth == 0 ? 0.0 : (double) uniqueDays / daysInMonth;
            double avgPerActiveWeek = weeksActive == 0 ? 0.0 : (double) totalCompletions / weeksActive;

            out.add(new MonthlyCategoryStatDTO(
                    y, m, category,
                    totalCompletions, uniqueDays,
                    round2(avgPerActiveDay), totalDurationMins,
                    round4(activeDayRatio),
                    weeksActive, round2(avgPerActiveWeek)
            ));
        }
        return out;
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }
}
