package com.tarea.resolvers;

import com.tarea.dtos.RoutineHabitDTO;
import com.tarea.resolvers.inputs.RoutineHabitInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.RoutineHabitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineHabitResolverTest {
    
    @Mock private RoutineHabitService service;
    @InjectMocks private RoutineHabitResolver resolver;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getRoutineHabitsByRoutineId_returnsList() {
        List<RoutineHabitDTO> expectedList = List.of(new RoutineHabitDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(service.getByRoutineId(1L)).thenReturn(expectedList);
            
            List<RoutineHabitDTO> result = resolver.getRoutineHabitsByRoutineId(1L);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void createRoutineHabit_returnsDTO() {
        RoutineHabitInput input = new RoutineHabitInput();
        input.setRoutineId(2L);
        input.setHabitId(3L);
        RoutineHabitDTO expectedDto = new RoutineHabitDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(service.save(any(RoutineHabitDTO.class))).thenReturn(expectedDto);
            
            RoutineHabitDTO result = resolver.createRoutineHabit(input);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void deleteRoutineHabit_returnsTrue() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            doNothing().when(service).delete(4L, 5L);
            
            boolean result = resolver.deleteRoutineHabit(4L, 5L);
            assertTrue(result);
            verify(service).delete(4L, 5L);
        }
    }
}