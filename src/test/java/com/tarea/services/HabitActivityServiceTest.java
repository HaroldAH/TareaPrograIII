package com.tarea.services;

import com.tarea.dtos.HabitActivityDTO;
import com.tarea.models.Habitactivity;
import com.tarea.repositories.HabitActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HabitActivityServiceTest {

    private HabitActivityRepository repo;
    private HabitActivityService service;

    @BeforeEach
    void setUp() {
        repo = mock(HabitActivityRepository.class);
        service = new HabitActivityService(repo);
    }

    @Test
    void save_create_parsesTargetTime() {
        HabitActivityDTO dto = new HabitActivityDTO();
        dto.setName("Meditate");
        dto.setCategory("MENTAL");
        dto.setDuration(20);
        dto.setTargetTime("07:30");

        when(repo.save(any(Habitactivity.class))).thenAnswer(inv -> {
            Habitactivity h = inv.getArgument(0);
            h.setId(1L);
            return h;
        });

        HabitActivityDTO out = service.save(dto);

        assertEquals(1L, out.getId());
        verify(repo).save(argThat(h -> LocalTime.parse("07:30").equals(h.getTargetTime())));
    }
}
