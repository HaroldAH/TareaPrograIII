package com.tarea.services;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.repositories.CompletedActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private CompletedActivityRepository repo;
    private StatsService service;

    @BeforeEach
    void setUp() {
        repo = mock(CompletedActivityRepository.class);
        service = new StatsService(repo);
    }

    @Test
void monthlyCategoryStats_month_ok() {
   Object[] row = new Object[]{2025, 9, "MENTAL", 6, 4, 120, 3};

List<Object[]> rows = java.util.Collections.singletonList(row);
when(repo.monthlyCategoryStatsMonth(2025, 9, 1L)).thenReturn(rows);

    List<MonthlyCategoryStatDTO> out = service.monthlyCategoryStats(2025, 9, 1L);

    assertEquals(1, out.size());
    var s = out.get(0);
    assertEquals(2025, s.getYear());
    assertEquals(9, s.getMonth());
    assertEquals("MENTAL", s.getCategory());
    assertEquals(6, s.getTotalCompletions());
    assertEquals(4, s.getUniqueDays());
    assertEquals(120, s.getTotalDurationMinutes());
    assertEquals(3, s.getWeeksActive());
    assertEquals(1.5, s.getAvgPerActiveDay());
    assertEquals(0.1333, s.getActiveDayRatio());
    assertEquals(2.0, s.getAvgPerActiveWeek());
}

}
