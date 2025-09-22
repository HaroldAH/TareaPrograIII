package com.tarea.resolvers;

import com.tarea.dtos.GuideHabitDTO;
import com.tarea.resolvers.inputs.GuideHabitInput;
import com.tarea.services.GuideHabitService;
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
class GuideHabitResolverTest extends BaseResolverTest {
    @Mock private GuideHabitService guideHabitService;
    @InjectMocks private GuideHabitResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new GuideHabitResolver(guideHabitService);
        Authentication auth = mock(Authentication.class);
    when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.List.of(new SimpleGrantedAuthority("MOD:GUIDES:RW")));
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getAllGuideHabits_returnsList() {
        List<GuideHabitDTO> list = List.of(new GuideHabitDTO());
        when(guideHabitService.getAll()).thenReturn(list);
        assertEquals(list, resolver.getAllGuideHabits());
    }

    @Test
    void getGuideHabitById_returnsDTO() {
        GuideHabitDTO dto = new GuideHabitDTO();
        when(guideHabitService.getById(1L, 2L)).thenReturn(dto);
        assertEquals(dto, resolver.getGuideHabitById(1L, 2L));
    }

    @Test
    void createGuideHabit_returnsDTO() {
        GuideHabitInput input = new GuideHabitInput();
        input.setGuideId(3L);
        input.setHabitId(4L);
        GuideHabitDTO dto = new GuideHabitDTO();
        when(guideHabitService.save(any())).thenReturn(dto);
        assertEquals(dto, resolver.createGuideHabit(input));
    }

    @Test
    void deleteGuideHabit_returnsTrue() {
        doNothing().when(guideHabitService).delete(5L, 6L);
        assertTrue(resolver.deleteGuideHabit(5L, 6L));
    }
}
