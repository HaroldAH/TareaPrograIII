package com.tarea.services;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.models.Module;
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock CompletedActivityRepository repo;
    @InjectMocks StatsService service;

    @Test
    void nonStaff_nullUserId_uses_me() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
             
            sec.when(SecurityUtils::userId).thenReturn(10L);
            sec.when(() -> SecurityUtils.requireView(Module.PROGRESS))
               .thenThrow(new AccessDeniedException("NO_VIEW"));

            Object[] row = new Object[]{2025, 9, "MENTAL", 6, 3, 60, 2};
            List<Object[]> rows = new ArrayList<>();
            rows.add(row);
            when(repo.monthlyCategoryStatsMonth(2025, 9, 10L)).thenReturn(rows);

            List<MonthlyCategoryStatDTO> out = service.monthlyCategoryStats(2025, 9, null);
            assertThat(out).hasSize(1);
            MonthlyCategoryStatDTO d = out.get(0);
            assertThat(d.getTotalCompletions()).isEqualTo(6);
            assertThat(d.getAvgPerActiveDay()).isEqualTo(2.0f);   
            assertThat(d.getAvgPerActiveWeek()).isEqualTo(3.0f);  
        }
    }

    @Test
    void staff_nullUserId_global_ok() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
             
            sec.when(SecurityUtils::userId).thenReturn(1L);
            sec.when(() -> SecurityUtils.requireView(Module.PROGRESS)).thenAnswer(inv -> null);

            Object[] row = new Object[]{2025, 9, "PHYSICAL", 10, 5, 120, 4};
            List<Object[]> rows = new ArrayList<>();
            rows.add(row);
            when(repo.monthlyCategoryStatsMonth(2025, 9, null)).thenReturn(rows);

            List<MonthlyCategoryStatDTO> out = service.monthlyCategoryStats(2025, 9, null);
            assertThat(out).hasSize(1);
            MonthlyCategoryStatDTO d = out.get(0);
            assertThat(d.getCategory()).isEqualTo("PHYSICAL");
            assertThat(d.getTotalDurationMinutes()).isEqualTo(120);
        }
    }

    @Test
    void otherUser_requires_view_denied() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
            sec.when(SecurityUtils::userId).thenReturn(10L);
             
            sec.when(() -> SecurityUtils.requireView(Module.PROGRESS))
               .thenThrow(new AccessDeniedException("NO_VIEW"));

            assertThrows(AccessDeniedException.class,
                () -> service.monthlyCategoryStats(2025, 9, 99L));
            verifyNoInteractions(repo);
        }
    }
}
