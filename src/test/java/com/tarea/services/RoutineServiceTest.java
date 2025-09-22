package com.tarea.services;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.dtos.RoutineHabitDetailDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Routine;
import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import com.tarea.models.User;
import com.tarea.repositories.RoutineHabitRepository;
import com.tarea.repositories.RoutineRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RoutineServiceTest {

    private RoutineRepository routineRepo;
    private UserRepository userRepo;
    private RoutineHabitRepository rhRepo;
    private RoutineService service;

    @BeforeEach
    void setUp() {
        routineRepo = mock(RoutineRepository.class);
        userRepo = mock(UserRepository.class);
        rhRepo = mock(RoutineHabitRepository.class);
        service = new RoutineService(routineRepo, userRepo, rhRepo);
    }

    @Test
    void save_create_ok() {
        var dto = new RoutineDTO();
        dto.setTitle("Morning Flow");
        dto.setUserId(1L);
        dto.setDaysOfWeek("MON,WED,FRI");

        var u = new User();
        u.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(routineRepo.save(any(Routine.class))).thenAnswer(inv -> {
            Routine r = inv.getArgument(0);
            r.setId(11L);
            return r;
        });

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::userId).thenReturn(1L);
            // OJO: usa el nombre totalmente calificado para evitar la colisión con java.lang.Module
            security.when(() -> SecurityUtils.requireMutate(com.tarea.models.Module.ROUTINES))
                    .thenAnswer(inv -> null);

            RoutineDTO saved = service.save(dto);

            assertEquals(11L, saved.getId());
            assertEquals("Morning Flow", saved.getTitle());
            assertEquals("MON,WED,FRI", saved.getDaysOfWeek());
            assertEquals(1L, saved.getUserId());
        }
    }

    @Test
    void getRoutineDetail_buildsHabitList() {
        var r = new Routine();
        r.setId(5L);
        r.setTitle("R");
        r.setDaysOfWeek("MON");
        when(routineRepo.findById(5L)).thenReturn(Optional.of(r));

        var h = new Habitactivity();
        h.setId(2L);
        h.setName("Meditate");
        h.setCategory("MENTAL");
        h.setDescription("desc");

        var rh = new RoutineHabit();
        // RoutineHabitId NO tiene constructor (long,long): crea y setea campos
        RoutineHabitId id = new RoutineHabitId();
        id.setRoutineId(5L);
        id.setHabitId(2L);
        rh.setId(id);
        rh.setRoutine(r);
        rh.setHabit(h);
        rh.setOrderInRoutine(1);
        rh.setTargetTimeInRoutine(LocalTime.parse("07:30"));
        rh.setNotes("n");

        when(rhRepo.findByRoutine_IdOrderByOrderInRoutine(5L)).thenReturn(List.of(rh));

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            // Igual: nombre totalmente calificado para tu enum Module
            security.when(() -> SecurityUtils.requireView(com.tarea.models.Module.ROUTINES))
                    .thenAnswer(inv -> null);

            RoutineDetailDTO detail = service.getRoutineDetail(5L);

            assertEquals(5L, detail.getId());
            assertEquals(1, detail.getHabits().size());
            RoutineHabitDetailDTO item = detail.getHabits().get(0);
            assertEquals("Meditate", item.getName());
            assertEquals("07:30", item.getTargetTimeInRoutine());
            assertEquals(1, item.getOrderInRoutine());
        }
    }
}
