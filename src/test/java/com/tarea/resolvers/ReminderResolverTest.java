package com.tarea.resolvers;

import com.tarea.dtos.ReminderDTO;
import com.tarea.dtos.ReminderListDTO;
import com.tarea.models.Habitactivity;
import com.tarea.resolvers.inputs.ReminderInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderResolverTest {
    
    @Mock private ReminderService reminderService;
    @InjectMocks private ReminderResolver resolver;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllReminders_returnsList() {
        List<ReminderDTO> expectedList = List.of(new ReminderDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(reminderService.getAll()).thenReturn(expectedList);
            
            List<ReminderDTO> result = resolver.getAllReminders();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getReminderById_returnsDTO() {
        ReminderDTO expectedDto = new ReminderDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any())).thenAnswer(invocation -> null);
            when(reminderService.getById(1L)).thenReturn(expectedDto);
            
            ReminderDTO result = resolver.getReminderById(1L);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void getRemindersByUser_returnsList() {
        List<ReminderDTO> expectedList = List.of(new ReminderDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any())).thenAnswer(invocation -> null);
            when(reminderService.getByUserId(2L)).thenReturn(expectedList);
            
            List<ReminderDTO> result = resolver.getRemindersByUser(2L);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getMyReminders_returnsList() {
        List<ReminderDTO> expectedList = List.of(new ReminderDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(reminderService.getByUserId(1L)).thenReturn(expectedList);
            
            List<ReminderDTO> result = resolver.getMyReminders();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getMyReminderList_returnsList() {
        ReminderDTO reminder = new ReminderDTO();
        reminder.setId(1L);
        reminder.setFrequency("daily");
        reminder.setHabitId(5L);
        
        Habitactivity habit = new Habitactivity();
        habit.setId(5L);
        habit.setName("habit");
        habit.setCategory("cat");
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(reminderService.getByUserId(1L)).thenReturn(List.of(reminder));
            when(reminderService.getHabitById(5L)).thenReturn(Optional.of(habit));
            
            List<ReminderListDTO> result = resolver.getMyReminderList();
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals("daily", result.get(0).getFrequency());
            assertEquals(5L, result.get(0).getHabit().getId());
        }
    }


    @Test
    void createReminder_setsUserId() {
        ReminderInput input = new ReminderInput();
        input.setId(1L);
        input.setHabitId(2L);
        input.setTime("10:00");
        input.setFrequency("daily");
        ReminderDTO expectedDto = new ReminderDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            securityUtils.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any())).thenAnswer(invocation -> null);
            when(reminderService.save(any(ReminderDTO.class))).thenReturn(expectedDto);
            
            ReminderDTO result = resolver.createReminder(input);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void deleteReminder_returnsTrue() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any())).thenAnswer(invocation -> null);
            doNothing().when(reminderService).delete(7L);
            
            boolean result = resolver.deleteReminder(7L);
            assertTrue(result);
            verify(reminderService).delete(7L);
        }
    }


    @Test
    void createMyReminder_setsUserId() {
        ReminderInput input = new ReminderInput();
        input.setId(1L);
        input.setHabitId(2L);
        input.setTime("11:00");
        input.setFrequency("weekly");
        ReminderDTO expectedDto = new ReminderDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            securityUtils.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any())).thenAnswer(invocation -> null);
            when(reminderService.save(any(ReminderDTO.class))).thenReturn(expectedDto);
            
            ReminderDTO result = resolver.createMyReminder(input);
            assertEquals(expectedDto, result);
        }
    }
}