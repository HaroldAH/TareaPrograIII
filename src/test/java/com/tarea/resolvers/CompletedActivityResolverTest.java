package com.tarea.resolvers;

import com.tarea.dtos.CompletedActivityDTO;
import com.tarea.dtos.CompletedDayDTO;
import com.tarea.dtos.CompletedWeekDTO;
import com.tarea.resolvers.inputs.CompletedActivityInput;
import com.tarea.services.CompletedActivityService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompletedActivityResolverTest extends BaseResolverTest {
    @Mock private CompletedActivityService service;
    @InjectMocks private CompletedActivityResolver resolver;

    @Test
    void getAllCompletedActivities_returnsList() {
        List<CompletedActivityDTO> list = List.of(new CompletedActivityDTO());
        when(service.getAll()).thenReturn(list);
        assertEquals(list, resolver.getAllCompletedActivities());
    }

    @Test
    void getCompletedActivityById_returnsDTO() {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        when(service.getById(1L)).thenReturn(dto);
        assertEquals(dto, resolver.getCompletedActivityById(1L));
    }

    @Test
    void getCompletedActivitiesByUser_returnsList() {
        List<CompletedActivityDTO> list = List.of(new CompletedActivityDTO());
        when(service.getByUser(2L, "2023-01-01", "2023-01-31")).thenReturn(list);
        assertEquals(list, resolver.getCompletedActivitiesByUser(2L, "2023-01-01", "2023-01-31"));
    }

    @Test
    void getMyCompletedActivities_returnsList() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("5");
        List<CompletedActivityDTO> list = List.of(new CompletedActivityDTO());
        when(service.getByUser(5L, "2023-01-01", "2023-01-31")).thenReturn(list);
        assertEquals(list, resolver.getMyCompletedActivities("2023-01-01", "2023-01-31", auth));
    }

    @Test
    void getMyCompletedActivitiesPerDay_returnsList() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("6");
        List<CompletedDayDTO> list = List.of(new CompletedDayDTO());
        when(service.getCompletedByUserPerDay(6L, "2023-01-01", "2023-01-31")).thenReturn(list);
        assertEquals(list, resolver.getMyCompletedActivitiesPerDay("2023-01-01", "2023-01-31", auth));
    }

    @Test
    void getMyCompletedActivitiesPerWeek_returnsList() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("7");
        List<CompletedWeekDTO> list = List.of(new CompletedWeekDTO());
        when(service.getCompletedByUserPerWeek(7L, "2023-01-01", "2023-01-31")).thenReturn(list);
        assertEquals(list, resolver.getMyCompletedActivitiesPerWeek("2023-01-01", "2023-01-31", auth));
    }

    @Test
    void getMyCompletedActivitiesOnDay_returnsDTO() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("8");
        CompletedDayDTO dto = new CompletedDayDTO();
        when(service.getCompletedByUserOnDay(8L, "2023-01-15")).thenReturn(dto);
        assertEquals(dto, resolver.getMyCompletedActivitiesOnDay("2023-01-15", auth));
    }

    @Test
    void createCompletedActivity_setsUserIdIfNull() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("9");
        CompletedActivityInput input = new CompletedActivityInput();
        input.setUserId(null);
        CompletedActivityDTO dto = new CompletedActivityDTO();
        when(service.save(any())).thenReturn(dto);
        assertEquals(dto, resolver.createCompletedActivity(input, auth));
        assertEquals(9L, input.getUserId());
    }

    @Test
    void createCompletedActivity_keepsUserIdIfPresent() {
        Authentication auth = mock(Authentication.class);
        CompletedActivityInput input = new CompletedActivityInput();
        input.setUserId(10L);
        CompletedActivityDTO dto = new CompletedActivityDTO();
        when(service.save(any())).thenReturn(dto);
        assertEquals(dto, resolver.createCompletedActivity(input, auth));
        assertEquals(10L, input.getUserId());
    }

    @Test
    void deleteCompletedActivity_returnsTrue() {
        doNothing().when(service).delete(11L);
        assertTrue(resolver.deleteCompletedActivity(11L));
    }
}