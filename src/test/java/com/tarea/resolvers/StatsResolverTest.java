package com.tarea.resolvers;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.security.SecurityUtils;
import com.tarea.services.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsResolverTest {
    
    @Mock private StatsService statsService;
    @InjectMocks private StatsResolver resolver;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void monthlyCategoryStats_withUserId() {
        List<MonthlyCategoryStatDTO> expectedList = List.of(new MonthlyCategoryStatDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(statsService.monthlyCategoryStats(2025, 9, 1L)).thenReturn(expectedList);
            
            List<MonthlyCategoryStatDTO> result = resolver.monthlyCategoryStats(2025, 9, 1L, null);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void monthlyCategoryStats_withAuth() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("2");
        List<MonthlyCategoryStatDTO> expectedList = List.of(new MonthlyCategoryStatDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(statsService.monthlyCategoryStats(2025, 9, 2L)).thenReturn(expectedList);
            
            List<MonthlyCategoryStatDTO> result = resolver.monthlyCategoryStats(2025, 9, null, auth);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void monthlyCategoryStats_withNullUser() {
        List<MonthlyCategoryStatDTO> expectedList = List.of(new MonthlyCategoryStatDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(statsService.monthlyCategoryStats(2025, 9, null)).thenReturn(expectedList);
            
            List<MonthlyCategoryStatDTO> result = resolver.monthlyCategoryStats(2025, 9, null, null);
            assertEquals(expectedList, result);
        }
    }
}