package com.tarea.services;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Routine;
import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoutineHabitServiceTest {

    private RoutineHabitRepository rhRepo;
    private RoutineRepository routineRepo;
    private HabitActivityRepository habitRepo;
    private RoutineHabitService service;

    @BeforeEach
    void setUp() {
        rhRepo = mock(RoutineHabitRepository.class);
        routineRepo = mock(RoutineRepository.class);
        habitRepo = mock(HabitActivityRepository.class);
        service = new RoutineHabitService(rhRepo, routineRepo, habitRepo);
    }

    @Test
    void save_createsOrUpdates() {
        var dto = new RoutineHabitDTO();
        dto.setRoutineId(5L);
        dto.setHabitId(2L);
        dto.setOrderInRoutine(3);
        dto.setTargetTimeInRoutine("06:45");
        dto.setNotes("x");

        var r = new Routine(); r.setId(5L);
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
    }
}
