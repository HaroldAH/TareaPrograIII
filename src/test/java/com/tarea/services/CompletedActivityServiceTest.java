package com.tarea.services;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.models.Completedactivity;
import com.tarea.models.Habitactivity;
import com.tarea.models.User;
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CompletedActivityServiceTest {

    private CompletedActivityRepository repo;
    private UserRepository userRepo;
    private RoutineRepository routineRepo;
    private HabitActivityRepository habitRepo;
    private CompletedActivityService service;

    @BeforeEach
    void setUp() {
        repo = mock(CompletedActivityRepository.class);
        userRepo = mock(UserRepository.class);
        routineRepo = mock(RoutineRepository.class);
        habitRepo = mock(HabitActivityRepository.class);
        service = new CompletedActivityService(repo, userRepo, routineRepo, habitRepo);
    }

    @Test
    void save_create_ok() {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        dto.setUserId(1L);
        dto.setHabitId(2L);
        dto.setDate("2025-09-10");

        var u = new User(); u.setId(1L);
        var h = new Habitactivity(); h.setId(2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(habitRepo.findById(2L)).thenReturn(Optional.of(h));
        when(repo.save(any(Completedactivity.class))).thenAnswer(inv -> {
            Completedactivity e = inv.getArgument(0);
            e.setId(100L);
            return e;
        });

        CompletedActivityDTO out = service.save(dto);

        assertEquals(100L, out.getId());
        verify(repo).save(argThat(e -> LocalDate.parse("2025-09-10").equals(e.getDate())));
    }

    @Test
    void getCompletedByUserPerDay_groupsByDate() {
        var c1 = new Completedactivity(); c1.setId(1L);
        c1.setDate(LocalDate.parse("2025-09-10"));
        var c2 = new Completedactivity(); c2.setId(2L);
        c2.setDate(LocalDate.parse("2025-09-10"));

        when(repo.findByUser_IdAndDateBetween(eq(1L), any(), any())).thenReturn(List.of(c1, c2));

        List<CompletedDayDTO> days = service.getCompletedByUserPerDay(1L, "2025-09-10", "2025-09-10");

        assertEquals(1, days.size());
        assertEquals(2, days.get(0).getTotalCompleted());
        assertEquals("2025-09-10", days.get(0).getDate());
    }
}
