package com.tarea.services;

import com.tarea.dtos.ReminderDTO;
import com.tarea.models.Habitactivity;
import com.tarea.models.Reminder;
import com.tarea.models.User;
import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.ReminderRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReminderServiceTest {

    private ReminderRepository reminderRepo;
    private UserRepository userRepo;
    private HabitActivityRepository habitRepo;
    private ReminderService service;

    @BeforeEach
    void setUp() {
        reminderRepo = mock(ReminderRepository.class);
        userRepo = mock(UserRepository.class);
        habitRepo = mock(HabitActivityRepository.class);
        service = new ReminderService(reminderRepo, userRepo, habitRepo);
    }

    @Test
    void save_usesAuthenticatedUserWhenUserIdNull() {
        // given: dto SIN userId ⇒ el service debe tomar SecurityUtils.userId()
        ReminderDTO dto = new ReminderDTO();
        dto.setHabitId(2L);
        dto.setTime("07:30");
        dto.setFrequency("DAILY");

        var u = new User(); u.setId(1L);
        var h = new Habitactivity(); h.setId(2L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(habitRepo.findById(2L)).thenReturn(Optional.of(h));
        when(reminderRepo.save(any(Reminder.class))).thenAnswer(inv -> {
            Reminder r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::userId).thenReturn(1L); // <<< clave

            ReminderDTO out = service.save(dto);

            assertEquals(99L, out.getId());
            assertEquals(1L, out.getUserId());
            assertEquals(2L, out.getHabitId());
            assertEquals("07:30", out.getTime());
            assertEquals("DAILY", out.getFrequency());
        }
    }

    @Test
    void save_throwsAccessDenied_whenNoAuthenticatedUserAndUserIdNull() {
        ReminderDTO dto = new ReminderDTO();
        dto.setHabitId(2L);
        dto.setTime("07:30");
        dto.setFrequency("DAILY");

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::userId).thenThrow(new AccessDeniedException("Unauthorized"));

            assertThrows(AccessDeniedException.class, () -> service.save(dto));
        }
    }
}
