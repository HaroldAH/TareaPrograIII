package com.tarea.resolvers;

import com.tarea.dtos.GuideDTO;
import com.tarea.dtos.GuideDetailDTO;
import com.tarea.resolvers.inputs.GuideInput;
import com.tarea.services.GuideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.mockito.quality.Strictness;


@MockitoSettings(strictness = Strictness.LENIENT)
class GuideResolverTest extends BaseResolverTest {
    @Mock private GuideService guideService;
    @InjectMocks private GuideResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new GuideResolver(guideService);
        Authentication auth = mock(Authentication.class);
    when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.List.of(new SimpleGrantedAuthority("MOD:GUIDES:RW")));
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getAllGuides_returnsList() {
        List<GuideDTO> list = List.of(new GuideDTO());
        when(guideService.getAll()).thenReturn(list);
        assertEquals(list, resolver.getAllGuides());
    }

    @Test
    void getGuideById_returnsDTO() {
        GuideDTO dto = new GuideDTO();
        when(guideService.getById(1L)).thenReturn(dto);
        assertEquals(dto, resolver.getGuideById(1L));
    }

    @Test
    void getGuidesByCategory_returnsList() {
        List<GuideDTO> list = List.of(new GuideDTO());
        when(guideService.getByCategory("cat")).thenReturn(list);
        assertEquals(list, resolver.getGuidesByCategory("cat"));
    }

    @Test
    void getGuidesByUser_returnsList() {
        List<GuideDTO> list = List.of(new GuideDTO());
        when(guideService.getByUserId(2L)).thenReturn(list);
        assertEquals(list, resolver.getGuidesByUser(2L));
    }

    @Test
    void getGuidesByCategoryAndUser_returnsList() {
        List<GuideDTO> list = List.of(new GuideDTO());
        when(guideService.getByCategoryAndUserId("cat", 3L)).thenReturn(list);
        assertEquals(list, resolver.getGuidesByCategoryAndUser("cat", 3L));
    }

    @Test
    void getGuideDetail_returnsDetailDTO() {
        GuideDetailDTO dto = new GuideDetailDTO();
        when(guideService.getGuideDetail(4L)).thenReturn(dto);
        assertEquals(dto, resolver.getGuideDetail(4L));
    }

    @Test
    void createGuide_setsUserIdIfNull() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("5");
        GuideInput input = new GuideInput();
        input.setUserId(null);
        GuideDTO dto = new GuideDTO();
        when(guideService.save(any())).thenReturn(dto);
        assertEquals(dto, resolver.createGuide(input, auth));
        assertEquals(5L, input.getUserId());
    }

    @Test
    void createGuide_keepsUserIdIfPresent() {
        Authentication auth = mock(Authentication.class);
        GuideInput input = new GuideInput();
        input.setUserId(6L);
        GuideDTO dto = new GuideDTO();
        when(guideService.save(any())).thenReturn(dto);
        assertEquals(dto, resolver.createGuide(input, auth));
        assertEquals(6L, input.getUserId());
    }

    @Test
    void deleteGuide_returnsTrue() {
        doNothing().when(guideService).delete(7L);
        assertTrue(resolver.deleteGuide(7L));
    }
}
