package com.tarea.services;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.models.FavoriteHabit;
import com.tarea.models.Habitactivity;
import com.tarea.models.Module;
import com.tarea.repositories.FavoriteHabitRepository;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteHabitServiceTest {

    private FavoriteHabitRepository repo;
    private UserRepository userRepo;
    private HabitActivityRepository habitRepo;
    private FavoriteHabitService service;

    @BeforeEach
    void setUp() {
        repo = mock(FavoriteHabitRepository.class);
        userRepo = mock(UserRepository.class);
        habitRepo = mock(HabitActivityRepository.class);
        service = new FavoriteHabitService(repo, userRepo, habitRepo);
    }

    @Test
    void save_ok() {
        FavoriteHabitDTO dto = new FavoriteHabitDTO();
        dto.setUserId(1L);
        dto.setHabitId(2L);

        var u = new com.tarea.models.User(); u.setId(1L);
        var h = new Habitactivity(); h.setId(2L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(habitRepo.findById(2L)).thenReturn(Optional.of(h));
        when(repo.save(any(FavoriteHabit.class))).thenAnswer(inv -> {
            FavoriteHabit f = inv.getArgument(0);
            f.setId(9L);
            return f;
        });

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            // El service invoca userId() y requireSelfOrMutate(owner, HABITS)
            mock.when(SecurityUtils::userId).thenReturn(1L);
            mock.when(() -> SecurityUtils.requireSelfOrMutate(1L, Module.HABITS)).thenAnswer(inv -> null);

            FavoriteHabitDTO out = service.save(dto);

            assertEquals(9L, out.getId());
            assertEquals(2L, out.getHabitId());
        }
    }

    @Test
    void getFavoriteHabitsListByUser_mapsListDTO() {
        var h = new Habitactivity(); h.setId(2L); h.setName("Dormir"); h.setCategory("SLEEP");
        var fav = new FavoriteHabit(); fav.setId(1L);
        var u = new com.tarea.models.User(); u.setId(1L);
        fav.setUser(u); fav.setHabit(h);

        when(repo.findByUser_Id(1L)).thenReturn(List.of(fav));

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(() -> SecurityUtils.requireSelfOrView(1L, Module.HABITS)).thenAnswer(inv -> null);

            List<HabitActivityListDTO> list = service.getFavoriteHabitsListByUser(1L);

            assertEquals(1, list.size());
            assertEquals("Dormir", list.get(0).getName());
        }
    }

    @Test
    void getFavoriteHabitDetailByName_buildsDetailDTO() {
        var h = new Habitactivity();
        h.setId(2L); h.setName("Meditar"); h.setCategory("MENTAL");
        h.setDescription("desc"); h.setDuration(20); h.setTargetTime(LocalTime.parse("07:00"));
        var fav = new FavoriteHabit(); fav.setHabit(h);

        when(repo.findByUser_IdAndHabit_Name(1L, "Meditar")).thenReturn(fav);

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(() -> SecurityUtils.requireSelfOrView(1L, Module.HABITS)).thenAnswer(inv -> null);

            HabitActivityDTO dto = service.getFavoriteHabitDetailByName(1L, "Meditar");

            assertNotNull(dto);
            assertEquals("07:00", dto.getTargetTime());
            assertEquals(20, dto.getDuration());
        }
    }
}
