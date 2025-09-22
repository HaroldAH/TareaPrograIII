package com.tarea.resolvers;

import com.tarea.dtos.*;
import com.tarea.models.*;
import com.tarea.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class PagesResolverTest extends BaseResolverTest {
    @Mock private CompletedActivityRepository completedRepo;
    @Mock private HabitActivityRepository habitRepo;
    @Mock private GuideRepository guideRepo;
    @Mock private RoutineRepository routineRepo;
    @Mock private ReminderRepository reminderRepo;
    @Mock private FavoriteHabitRepository favoriteRepo;
    @Mock private RoutineHabitRepository routineHabitRepo;
    @Mock private GuideHabitRepository guideHabitRepo;
    @InjectMocks private PagesResolver resolver;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new PagesResolver(completedRepo, habitRepo, guideRepo, routineRepo, reminderRepo, favoriteRepo, routineHabitRepo, guideHabitRepo);
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.List.of(
            new SimpleGrantedAuthority("AUDITOR"),
            new SimpleGrantedAuthority("MOD:HABITS:RW"),
            new SimpleGrantedAuthority("MOD:GUIDES:RW"),
            new SimpleGrantedAuthority("MOD:PROGRESS:RW"),
            new SimpleGrantedAuthority("MOD:USERS:RW"),
            new SimpleGrantedAuthority("MOD:ROUTINES:RW"),
            new SimpleGrantedAuthority("MOD:REMINDERS:RW")
        ));
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void completedActivitiesPage_returnsPageDTO() {
        Completedactivity entity = new Completedactivity();
        Page<Completedactivity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(completedRepo.findAll(any(Pageable.class))).thenReturn(page);
        CompletedActivityPageDTO dto = resolver.completedActivitiesPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void habitActivitiesPage_returnsPageDTO() {
        Habitactivity entity = new Habitactivity();
        Page<Habitactivity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(habitRepo.findAll(any(Pageable.class))).thenReturn(page);
        HabitActivityPageDTO dto = resolver.habitActivitiesPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void guidesPage_returnsPageDTO() {
        Guide entity = new Guide();
        Page<Guide> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(guideRepo.findAll(any(Pageable.class))).thenReturn(page);
        GuidePageDTO dto = resolver.guidesPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void routinesPage_returnsPageDTO() {
        Routine entity = new Routine();
        Page<Routine> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(routineRepo.findAll(any(Pageable.class))).thenReturn(page);
        RoutinePageDTO dto = resolver.routinesPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void remindersPage_returnsPageDTO() {
        Reminder entity = new Reminder();
        Page<Reminder> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(reminderRepo.findAll(any(Pageable.class))).thenReturn(page);
        ReminderPageDTO dto = resolver.remindersPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void favoriteHabitsPage_returnsPageDTO() {
        FavoriteHabit entity = new FavoriteHabit();
        Page<FavoriteHabit> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(favoriteRepo.findAll(any(Pageable.class))).thenReturn(page);
        FavoriteHabitPageDTO dto = resolver.favoriteHabitsPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void routineHabitsPage_returnsPageDTO() {
        RoutineHabit entity = new RoutineHabit();
        Page<RoutineHabit> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(routineHabitRepo.findAll(any(Pageable.class))).thenReturn(page);
        RoutineHabitPageDTO dto = resolver.routineHabitsPage(null);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    void guideHabitsPage_returnsPageDTO() {
        GuideHabit entity = new GuideHabit();
        Page<GuideHabit> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1), 1);
        when(guideHabitRepo.findAll(any(Pageable.class))).thenReturn(page);
        GuideHabitPageDTO dto = resolver.guideHabitsPage(null);
        assertEquals(1, dto.getContent().size());
    }
}