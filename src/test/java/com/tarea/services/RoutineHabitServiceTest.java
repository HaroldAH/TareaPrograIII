package com.tarea.services;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;
import com.tarea.models.Routine;
import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import com.tarea.models.User;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

class RoutineHabitServiceTest {

    private RoutineHabitRepository rhRepo;
    private RoutineRepository routineRepo;
    private HabitActivityRepository habitRepo;
    private RoutineHabitService service;

    private MockedStatic<SecurityUtils> security;

    @BeforeEach
    void setUp() {
        rhRepo = mock(RoutineHabitRepository.class);
        routineRepo = mock(RoutineRepository.class);
        habitRepo = mock(HabitActivityRepository.class);
        service = new RoutineHabitService(rhRepo, routineRepo, habitRepo);

        security = mockStatic(SecurityUtils.class);
        security.when(SecurityUtils::isAuditor).thenReturn(false);
        security.when(() -> SecurityUtils.forbidAuditorWrites()).thenAnswer(inv -> null);
        security.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any(Module.class))).thenAnswer(inv -> null);
        security.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any(Module.class))).thenAnswer(inv -> null);
    }

    @AfterEach
    void tearDown() {
        if (security != null) security.close();
    }

    @Test
    void save_createsOrUpdates() {
        var dto = new RoutineHabitDTO();
        dto.setRoutineId(5L);
        dto.setHabitId(2L);
        dto.setOrderInRoutine(3);
        dto.setTargetTimeInRoutine("06:45");
        dto.setNotes("x");

        var user = new User(); user.setId(1L);
        var r = new Routine(); r.setId(5L); r.setUser(user);
        var h = new Habitactivity(); h.setId(2L);

        when(routineRepo.findById(5L)).thenReturn(Optional.of(r));
        when(habitRepo.findById(2L)).thenReturn(Optional.of(h));
        when(rhRepo.findById(any(RoutineHabitId.class))).thenReturn(Optional.empty());
        when(rhRepo.save(any(RoutineHabit.class))).thenAnswer(inv -> inv.getArgument(0));

        var out = service.save(dto);

        assertEquals(5L, out.getRoutineId());
        assertEquals(2L, out.getHabitId());
        assertEquals(3, out.getOrderInRoutine());
        assertEquals("06:45", out.getTargetTimeInRoutine());

        verify(rhRepo).save(argThat(rh ->
                rh.getRoutine().getId().equals(5L) &&
                rh.getHabit().getId().equals(2L) &&
                rh.getOrderInRoutine() == 3 &&
                LocalTime.parse("06:45").equals(rh.getTargetTimeInRoutine()) &&
                "x".equals(rh.getNotes())
        ));
    }

    @Test
    void save_updatesExistingRoutineHabit() {
        var dto = new RoutineHabitDTO();
        dto.setRoutineId(7L);
        dto.setHabitId(4L);
        dto.setOrderInRoutine(2);
        dto.setTargetTimeInRoutine("08:10");
        dto.setNotes("edit");

        var user = new User(); user.setId(10L);
        var routine = new Routine(); routine.setId(7L); routine.setUser(user);
        var habit = new Habitactivity(); habit.setId(4L);

        var existing = new RoutineHabit();
        existing.setRoutine(routine);
        existing.setHabit(habit);
        existing.setOrderInRoutine(1);
        existing.setTargetTimeInRoutine(LocalTime.parse("07:00"));
        existing.setNotes("old");

        when(routineRepo.findById(7L)).thenReturn(Optional.of(routine));
        when(habitRepo.findById(4L)).thenReturn(Optional.of(habit));
        when(rhRepo.findById(any(RoutineHabitId.class))).thenReturn(Optional.of(existing));
        when(rhRepo.save(any(RoutineHabit.class))).thenAnswer(inv -> inv.getArgument(0));

        var out = service.save(dto);

        assertEquals(7L, out.getRoutineId());
        assertEquals(4L, out.getHabitId());
        assertEquals(2, out.getOrderInRoutine());
        assertEquals("08:10", out.getTargetTimeInRoutine());

        verify(rhRepo).save(argThat(rh ->
                rh.getOrderInRoutine() == 2 &&
                LocalTime.parse("08:10").equals(rh.getTargetTimeInRoutine()) &&
                "edit".equals(rh.getNotes())
        ));
    }

    @Test
    void save_throwsIfRoutineNotFound() {
        var dto = new RoutineHabitDTO();
        dto.setRoutineId(100L);
        dto.setHabitId(2L);

        when(routineRepo.findById(100L)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("rutina no encontrada"));
        verifyNoInteractions(habitRepo, rhRepo);
    }

    @Test
    void save_throwsIfHabitNotFound() {
        var dto = new RoutineHabitDTO();
        dto.setRoutineId(5L);
        dto.setHabitId(999L);

        var user = new User(); user.setId(1L);
        var r = new Routine(); r.setId(5L); r.setUser(user);

        when(routineRepo.findById(5L)).thenReturn(Optional.of(r));
        when(habitRepo.findById(999L)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("hábito no encontrado"));
        verify(rhRepo, never()).save(any());
    }
}
