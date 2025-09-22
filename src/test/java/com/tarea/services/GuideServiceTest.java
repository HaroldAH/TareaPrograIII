package com.tarea.services;

import com.tarea.dtos.GuideDTO;
import com.tarea.dtos.GuideDetailDTO;
import com.tarea.models.*;
import com.tarea.repositories.GuideHabitRepository;
import com.tarea.repositories.GuideRepository;
import com.tarea.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GuideServiceTest {

    private GuideRepository guideRepo;
    private UserRepository userRepo;
    private GuideHabitRepository ghRepo;
    private GuideService service;

    @BeforeEach
    void setUp() {
        guideRepo = mock(GuideRepository.class);
        userRepo = mock(UserRepository.class);
        ghRepo = mock(GuideHabitRepository.class);
        service = new GuideService(guideRepo, userRepo, ghRepo);
    }

    @Test
    void save_create_ok() {
        GuideDTO dto = new GuideDTO();
        dto.setUserId(1L);
        dto.setTitle("T");
        dto.setContent("C");
        dto.setCategory("MENTAL");
        var u = new User(); u.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(guideRepo.save(any(Guide.class))).thenAnswer(inv -> {
            Guide g = inv.getArgument(0);
            g.setId(7L);
            return g;
        });

        GuideDTO out = service.save(dto);
        assertEquals(7L, out.getId());
        assertEquals("T", out.getTitle());
    }

    @Test
    void getGuideDetail_mapsHabits() {
        var g = new Guide(); g.setId(3L); g.setTitle("GT"); g.setContent("GC"); g.setCategory("MENTAL");
        var u = new User(); u.setId(1L); g.setUser(u);
        when(guideRepo.findById(3L)).thenReturn(Optional.of(g));

        var h = new Habitactivity(); h.setId(5L); h.setName("Meditate"); h.setCategory("MENTAL");

        var gh = new GuideHabit();
        gh.setId(new GuideHabitId(3L, 5L));
        gh.setGuide(g);
        gh.setHabit(h);

        when(ghRepo.findAll()).thenReturn(List.of(gh));

        GuideDetailDTO detail = service.getGuideDetail(3L);
        assertEquals(1, detail.getRecommendHabit().size());
        assertEquals("Meditate", detail.getRecommendHabit().get(0).getName());
    }
}
