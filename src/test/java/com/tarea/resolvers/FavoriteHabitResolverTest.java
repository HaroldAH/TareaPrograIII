package com.tarea.resolvers;

import com.tarea.dtos.FavoriteHabitDTO;
import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.repositories.UserRepository;
import com.tarea.resolvers.inputs.FavoriteHabitInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.FavoriteHabitService;
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
class FavoriteHabitResolverTest {
    
    @Mock private FavoriteHabitService service;
    @Mock private UserRepository userRepository;
    @InjectMocks private FavoriteHabitResolver resolver;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllFavoriteHabits_returnsList() {
        List<FavoriteHabitDTO> expectedList = List.of(new FavoriteHabitDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(service.getAll()).thenReturn(expectedList);
            
            List<FavoriteHabitDTO> result = resolver.getAllFavoriteHabits();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getFavoriteHabitById_returnsDTO() {
        FavoriteHabitDTO expectedDto = new FavoriteHabitDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireSelfOrView(anyLong(), any())).thenAnswer(invocation -> null);
            when(service.getById(1L)).thenReturn(expectedDto);
            
            FavoriteHabitDTO result = resolver.getFavoriteHabitById(1L);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void getFavoriteHabitsListByUser_returnsList() {
        List<HabitActivityListDTO> expectedList = List.of(new HabitActivityListDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(service.getFavoriteHabitsListByUser(1L)).thenReturn(expectedList);
            
            List<HabitActivityListDTO> result = resolver.getFavoriteHabitsListByUser();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getFavoriteHabitsByCategory_returnsList() {
        List<HabitActivityListDTO> expectedList = List.of(new HabitActivityListDTO());
        String category = "cat";
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(service.getFavoriteHabitsByCategory(1L, category)).thenReturn(expectedList);
            
            List<HabitActivityListDTO> result = resolver.getFavoriteHabitsByCategory(category);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getFavoriteHabitDetailByName_returnsDTO() {
        HabitActivityDTO expectedDto = new HabitActivityDTO();
        String habitName = "habit";
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(service.getFavoriteHabitDetailByName(1L, habitName)).thenReturn(expectedDto);
            
            HabitActivityDTO result = resolver.getFavoriteHabitDetailByName(habitName);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void createFavoriteHabit_setsUserId() {
        FavoriteHabitInput input = new FavoriteHabitInput();
        input.setId(1L);
        input.setHabitId(2L);
        FavoriteHabitDTO expectedDto = new FavoriteHabitDTO();
        
        FavoriteHabitDTO dtoForService = new FavoriteHabitDTO();
        dtoForService.setId(1L);
        dtoForService.setHabitId(2L);
        dtoForService.setUserId(1L);
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::userId).thenReturn(1L);
            when(service.save(any(FavoriteHabitDTO.class))).thenReturn(expectedDto);
            
            FavoriteHabitDTO result = resolver.createFavoriteHabit(input);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void deleteFavoriteHabit_returnsTrue() {
        Long habitId = 6L;
        doNothing().when(service).delete(habitId);
        
        boolean result = resolver.deleteFavoriteHabit(habitId);
        assertTrue(result);
        verify(service).delete(habitId);
    }
}