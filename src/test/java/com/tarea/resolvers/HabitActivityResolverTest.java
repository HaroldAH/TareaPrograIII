package com.tarea.resolvers;

import com.tarea.dtos.HabitActivityDTO;
import com.tarea.dtos.HabitActivityListDTO;
import com.tarea.resolvers.inputs.HabitActivityInput;
import com.tarea.services.HabitActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class HabitActivityResolverTest extends BaseResolverTest {
    @Mock private HabitActivityService service;
    @InjectMocks private HabitActivityResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new HabitActivityResolver(service);
         
        Authentication auth = mock(Authentication.class);
    when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.List.of(new SimpleGrantedAuthority("MOD:HABITS:RW")));
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getAllHabitActivities_returnsList() {
        List<HabitActivityDTO> list = List.of(new HabitActivityDTO());
        when(service.getAll()).thenReturn(list);
        assertEquals(list, resolver.getAllHabitActivities());
    }

    @Test
    void getHabitActivitiesByCategory_returnsList() {
        List<HabitActivityListDTO> list = List.of(new HabitActivityListDTO());
        when(service.getByCategory("cat")).thenReturn(list);
        assertEquals(list, resolver.getHabitActivitiesByCategory("cat"));
    }

    @Test
    void getHabitActivityById_returnsDTO() {
        HabitActivityDTO dto = new HabitActivityDTO();
        when(service.getById(1L)).thenReturn(dto);
        assertEquals(dto, resolver.getHabitActivityById(1L));
    }

    @Test
    void getHabitActivityByName_returnsDTO() {
        HabitActivityDTO dto = new HabitActivityDTO();
        when(service.getByName("name")).thenReturn(dto);
        assertEquals(dto, resolver.getHabitActivityByName("name"));
    }

    @Test
    void createHabitActivity_returnsDTO() {
        HabitActivityInput input = new HabitActivityInput();
        input.setId(2L);
        HabitActivityDTO dto = new HabitActivityDTO();
        when(service.save(any())).thenReturn(dto);
        assertEquals(dto, resolver.createHabitActivity(input));
    }

    @Test
    void deleteHabitActivity_returnsTrue() {
        doNothing().when(service).delete(3L);
        assertTrue(resolver.deleteHabitActivity(3L));
    }

    @Test
    void getAllHabitActivitiesAsList_returnsList() {
        List<HabitActivityListDTO> list = List.of(new HabitActivityListDTO());
        when(service.getAllAsList()).thenReturn(list);
        assertEquals(list, resolver.getAllHabitActivitiesAsList());
    }
}
