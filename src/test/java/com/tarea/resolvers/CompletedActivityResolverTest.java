package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletedActivityResolverTest {

    @Mock private CompletedActivityService service;
    @InjectMocks private CompletedActivityResolver resolver;

    private CompletedActivityDTO dto1;

    @BeforeEach
    void init() {
        dto1 = new CompletedActivityDTO();
        dto1.setId(1L);
        dto1.setUserId(10L);
        dto1.setHabitId(2L);
        dto1.setDate("2025-09-22");
        dto1.setCompletedAt("07:45");
        dto1.setNotes("ok");
    }

    @Test
    void getAllCompletedActivities_ok() {
        when(service.getAll()).thenReturn(List.of(dto1));
        var out = resolver.getAllCompletedActivities();
        assertThat(out).hasSize(1);
        verify(service).getAll();
    }

    @Test
    void getCompletedActivityById_ok() {
        when(service.getById(1L)).thenReturn(dto1);
        var out = resolver.getCompletedActivityById(1L);
        assertThat(out.getId()).isEqualTo(1L);
        verify(service).getById(1L);
    }

    @Test
    void getCompletedActivitiesByUser_ok() {
        when(service.getByUser(10L, "2025-09-01", "2025-09-30")).thenReturn(List.of(dto1));
        var out = resolver.getCompletedActivitiesByUser(10L, "2025-09-01", "2025-09-30");
        assertThat(out).hasSize(1);
        verify(service).getByUser(10L, "2025-09-01", "2025-09-30");
    }

    @Test
    void myQueries_ok() {
        // getMyCompletedActivities
        when(service.getMine("2025-09-01", "2025-09-30")).thenReturn(List.of(dto1));
        var m1 = resolver.getMyCompletedActivities("2025-09-01", "2025-09-30");
        assertThat(m1).hasSize(1);
        verify(service).getMine("2025-09-01", "2025-09-30");

        // getMyCompletedActivitiesPerDay
        CompletedDayDTO dPerDay = new CompletedDayDTO();
        dPerDay.setDate("2025-09-10");
        dPerDay.setActivities(List.of(dto1));
        dPerDay.setTotalCompleted(1);
        when(service.getMinePerDay("2025-09-01", "2025-09-30")).thenReturn(List.of(dPerDay));
        var m2 = resolver.getMyCompletedActivitiesPerDay("2025-09-01", "2025-09-30");
        assertThat(m2).hasSize(1);
        assertThat(m2.get(0).getDate()).isEqualTo("2025-09-10");
        verify(service).getMinePerDay("2025-09-01", "2025-09-30");

        // getMyCompletedActivitiesPerWeek
        CompletedWeekDTO w = new CompletedWeekDTO();
        w.setWeekLabel("2025-W38");
        w.setTotalCompleted(5);
        w.setRoutines(List.of());
        when(service.getMinePerWeek("2025-09-01", "2025-09-30")).thenReturn(List.of(w));
        var m3 = resolver.getMyCompletedActivitiesPerWeek("2025-09-01", "2025-09-30");
        assertThat(m3).hasSize(1);
        assertThat(m3.get(0).getWeekLabel()).isEqualTo("2025-W38");
        verify(service).getMinePerWeek("2025-09-01", "2025-09-30");

        // getMyCompletedActivitiesOnDay
        CompletedDayDTO dOnDay = new CompletedDayDTO();
        dOnDay.setDate("2025-09-22");
        dOnDay.setActivities(List.of(dto1));
        dOnDay.setTotalCompleted(1);
        when(service.getMineOnDay("2025-09-22")).thenReturn(dOnDay);
        var m4 = resolver.getMyCompletedActivitiesOnDay("2025-09-22");
        assertThat(m4.getDate()).isEqualTo("2025-09-22");
        verify(service).getMineOnDay("2025-09-22");
    }

    @Test
    void createCompletedActivity_ok() {
        CompletedActivityInput in = new CompletedActivityInput();
        in.setHabitId(2L);
        in.setDate("2025-09-22");
        in.setCompletedAt("07:45");
        in.setNotes("ok");

        when(service.save(any())).thenReturn(dto1);
        var out = resolver.createCompletedActivity(in);
        assertThat(out.getId()).isEqualTo(1L);
        verify(service).save(argThat(d ->
            d.getHabitId().equals(2L) &&
            "2025-09-22".equals(d.getDate()) &&
            "07:45".equals(d.getCompletedAt())
        ));
    }

    @Test
    void deleteCompletedActivity_ok() {
        var result = resolver.deleteCompletedActivity(1L);
        assertThat(result).isTrue();
        verify(service).delete(1L);
    }
}
