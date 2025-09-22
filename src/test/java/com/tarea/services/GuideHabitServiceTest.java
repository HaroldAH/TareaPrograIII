package com.tarea.services;

import com.tarea.dtos.GuideHabitDTO;
import com.tarea.models.*;
import com.tarea.repositories.GuideHabitRepository;
import com.tarea.repositories.GuideRepository;
import com.tarea.repositories.HabitActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GuideHabitServiceTest {

    private GuideHabitRepository repo;
    private GuideRepository guideRepo;
    private HabitActivityRepository habitRepo;
    private GuideHabitService service;

    @BeforeEach
    void setUp() {
        repo = mock(GuideHabitRepository.class);
        guideRepo = mock(GuideRepository.class);
        habitRepo = mock(HabitActivityRepository.class);
        service = new GuideHabitService(repo, guideRepo, habitRepo);
    }

    @Test
    void save_ok() {
        GuideHabitDTO dto = new GuideHabitDTO();
        dto.setGuideId(3L);
        dto.setHabitId(5L);

        var g = new Guide(); g.setId(3L);
        var h = new Habitactivity(); h.setId(5L);
        when(guideRepo.findById(3L)).thenReturn(Optional.of(g));
        when(habitRepo.findById(5L)).thenReturn(Optional.of(h));
        when(repo.save(any(GuideHabit.class))).thenAnswer(inv -> inv.getArgument(0));

        GuideHabitDTO out = service.save(dto);

        assertEquals(3L, out.getGuideId());
        assertEquals(5L, out.getHabitId());
    }
}
