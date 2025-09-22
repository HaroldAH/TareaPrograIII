package com.tarea.resolvers;

import com.tarea.dtos.RoutineDTO;
import com.tarea.dtos.RoutineDetailDTO;
import com.tarea.resolvers.inputs.RoutineInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.RoutineService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineResolverTest {
    
    @Mock private RoutineService routineService;
    @InjectMocks private RoutineResolver resolver;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllRoutines_returnsList() {
        List<RoutineDTO> expectedList = List.of(new RoutineDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(routineService.getAll()).thenReturn(expectedList);
            
            List<RoutineDTO> result = resolver.getAllRoutines();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getRoutineById_returnsDTO() {
        RoutineDTO expectedDto = new RoutineDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any())).thenAnswer(invocation -> null);
            when(routineService.getById(1L)).thenReturn(expectedDto);
            
            RoutineDTO result = resolver.getRoutineById(1L);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void getRoutinesByUser_returnsList() {
        List<RoutineDTO> expectedList = List.of(new RoutineDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any())).thenAnswer(invocation -> null);
            when(routineService.getByUserId(2L)).thenReturn(expectedList);
            
            List<RoutineDTO> result = resolver.getRoutinesByUser(2L);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getMyRoutines_returnsList() {
        List<RoutineDTO> expectedList = List.of(new RoutineDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(routineService.getByUserId(1L)).thenReturn(expectedList);
            
            List<RoutineDTO> result = resolver.getMyRoutines();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getRoutineDetail_returnsDetailDTO() {
        RoutineDetailDTO expectedDto = new RoutineDetailDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any())).thenAnswer(invocation -> null);
            when(routineService.getRoutineDetail(4L)).thenReturn(expectedDto);
            
            RoutineDetailDTO result = resolver.getRoutineDetail(4L);
            assertEquals(expectedDto, result);
        }
    }


    @Test
    void createRoutine_setsUserIdIfNull() {
        RoutineInput input = new RoutineInput();
        input.setUserId(null);
        RoutineDTO expectedDto = new RoutineDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            securityUtils.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any())).thenAnswer(invocation -> null);
            when(routineService.save(any(RoutineDTO.class))).thenReturn(expectedDto);
            
            RoutineDTO result = resolver.createRoutine(input);
            assertEquals(expectedDto, result);
            assertEquals(1L, input.getUserId());
        }
    }

    @Test
    void createRoutine_keepsUserIdIfPresent() {
        RoutineInput input = new RoutineInput();
        input.setUserId(6L);
        RoutineDTO expectedDto = new RoutineDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any())).thenAnswer(invocation -> null);
            when(routineService.save(any(RoutineDTO.class))).thenReturn(expectedDto);
            
            RoutineDTO result = resolver.createRoutine(input);
            assertEquals(expectedDto, result);
            assertEquals(6L, input.getUserId());
        }
    }

    @Test
    void deleteRoutine_returnsTrue() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrMutate(anyLong(), any())).thenAnswer(invocation -> null);
            doNothing().when(routineService).delete(7L);
            
            boolean result = resolver.deleteRoutine(7L);
            assertTrue(result);
            verify(routineService).delete(7L);
        }
    }
}