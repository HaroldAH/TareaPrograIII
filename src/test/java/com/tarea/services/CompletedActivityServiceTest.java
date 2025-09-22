package com.tarea.services;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.models.Completedactivity;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;
import com.tarea.models.User;
import com.tarea.repositories.CompletedActivityRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletedActivityServiceTest {

    @Mock CompletedActivityRepository repo;
    @Mock UserRepository userRepo;
    @Mock RoutineRepository routineRepo;
    @Mock HabitActivityRepository habitRepo;

    @InjectMocks CompletedActivityService service;

    @Test
    void save_own_without_mutate_ok() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
            // Soy el 10
            sec.when(SecurityUtils::userId).thenReturn(10L);
            // No soy auditor y no se exige mutate para self
            sec.when(() -> SecurityUtils.forbidAuditorWrites()).thenAnswer(inv -> null);
            sec.when(() -> SecurityUtils.requireSelfOrMutate(10L, Module.PROGRESS)).thenAnswer(inv -> null);

            User u = new User(); u.setId(10L);
            Habitactivity h = new Habitactivity(); h.setId(2L);

            when(userRepo.findById(10L)).thenReturn(Optional.of(u));
            when(habitRepo.findById(2L)).thenReturn(Optional.of(h));
            when(repo.save(any(Completedactivity.class))).thenAnswer(inv -> {
                Completedactivity e = inv.getArgument(0);
                e.setId(123L);
                return e;
            });

            CompletedActivityDTO dto = new CompletedActivityDTO();
            dto.setHabitId(2L);
            dto.setDate("2025-09-22");
            dto.setCompletedAt("07:45");

            var out = service.save(dto);
            assertThat(out.getId()).isEqualTo(123L);
            verify(repo).save(argThat(e ->
                e.getUser().getId().equals(10L) &&
                e.getHabit().getId().equals(2L) &&
                LocalDate.parse("2025-09-22").equals(e.getDate()) &&
                "07:45".equals(e.getCompletedAt())
            ));
        }
    }

    @Test
    void save_other_requires_mutate_denied() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
            sec.when(SecurityUtils::userId).thenReturn(10L);
            sec.when(() -> SecurityUtils.forbidAuditorWrites()).thenAnswer(inv -> null);
            // target es 999 (otro user) → lanzamos AccessDenied
            sec.when(() -> SecurityUtils.requireSelfOrMutate(999L, Module.PROGRESS))
               .thenThrow(new AccessDeniedException("UNAUTHORIZED"));

            CompletedActivityDTO dto = new CompletedActivityDTO();
            dto.setUserId(999L);
            dto.setHabitId(2L);
            dto.setDate("2025-09-22");

            assertThrows(AccessDeniedException.class, () -> service.save(dto));
            verifyNoInteractions(repo);
        }
    }

    @Test
    void getAll_requires_view() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
            sec.when(() -> SecurityUtils.requireView(Module.PROGRESS))
               .thenThrow(new AccessDeniedException("NO_VIEW"));
            assertThrows(AccessDeniedException.class, () -> service.getAll());
        }
    }

    @Test
    void getCompletedByUserPerDay_self_ok() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
            sec.when(SecurityUtils::userId).thenReturn(10L);
            // self-or-view para 10 → no lanza
            sec.when(() -> SecurityUtils.requireSelfOrView(10L, Module.PROGRESS)).thenAnswer(inv -> null);

            Completedactivity c1 = new Completedactivity();
            c1.setId(1L); c1.setDate(LocalDate.parse("2025-09-10"));
            Completedactivity c2 = new Completedactivity();
            c2.setId(2L); c2.setDate(LocalDate.parse("2025-09-10"));

            when(repo.findByUser_IdAndDateBetween(10L,
                LocalDate.parse("2025-09-01"), LocalDate.parse("2025-09-30")))
                .thenReturn(List.of(c1, c2));

            List<CompletedDayDTO> days = service.getCompletedByUserPerDay(10L, "2025-09-01", "2025-09-30");
            assertThat(days).hasSize(1);
            assertThat(days.get(0).getTotalCompleted()).isEqualTo(2);
            assertThat(days.get(0).getDate()).isEqualTo("2025-09-10");
        }
    }

    @Test
    void delete_own_ok() {
        try (MockedStatic<SecurityUtils> sec = Mockito.mockStatic(SecurityUtils.class)) {
            sec.when(() -> SecurityUtils.forbidAuditorWrites()).thenAnswer(inv -> null);

            Completedactivity e = new Completedactivity();
            User u = new User(); u.setId(10L); e.setUser(u);
            when(repo.findById(5L)).thenReturn(Optional.of(e));

            sec.when(() -> SecurityUtils.requireSelfOrMutate(10L, Module.PROGRESS)).thenAnswer(inv -> null);

            service.delete(5L);
            verify(repo).delete(e);
        }
    }
}
