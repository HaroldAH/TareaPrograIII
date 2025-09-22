package com.tarea.resolvers;

import com.tarea.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class CountResolverTest extends BaseResolverTest {
    @Mock private UserRepository userRepo;
    @Mock private HabitActivityRepository habitRepo;
    @Mock private GuideRepository guideRepo;
    @Mock private CompletedActivityRepository completedRepo;
    @Mock private RoutineRepository routineRepo;
    @Mock private ReminderRepository reminderRepo;
    @Mock private FavoriteHabitRepository favoriteRepo;
    @Mock private RoutineHabitRepository routineHabitRepo;
    @Mock private GuideHabitRepository guideHabitRepo;
    @InjectMocks private CountResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new CountResolver(userRepo, habitRepo, guideRepo, completedRepo, routineRepo, reminderRepo, favoriteRepo, routineHabitRepo, guideHabitRepo);
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.Arrays.asList(
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
    void countHabitActivities_returnsCount() {
        when(habitRepo.count()).thenReturn(5L);
        assertEquals(5, resolver.countHabitActivities());
    }

    @Test
    void countGuides_returnsCount() {
        when(guideRepo.count()).thenReturn(3L);
        assertEquals(3, resolver.countGuides());
    }

    @Test
    void countCompletedActivities_returnsCount() {
        when(completedRepo.count()).thenReturn(7L);
        assertEquals(7, resolver.countCompletedActivities());
    }

    @Test
    void countUsers_returnsCount() {
        when(userRepo.count()).thenReturn(10L);
        assertEquals(10, resolver.countUsers());
    }

    @Test
    void countRoutines_returnsCount() {
        when(routineRepo.count()).thenReturn(4L);
        assertEquals(4, resolver.countRoutines());
    }

    @Test
    void countReminders_returnsCount() {
        when(reminderRepo.count()).thenReturn(2L);
        assertEquals(2, resolver.countReminders());
    }

    @Test
    void countFavoriteHabits_returnsCount() {
        when(favoriteRepo.count()).thenReturn(6L);
        assertEquals(6, resolver.countFavoriteHabits());
    }

    @Test
    void countRoutineHabits_returnsCount() {
        when(routineHabitRepo.count()).thenReturn(8L);
        assertEquals(8, resolver.countRoutineHabits());
    }

    @Test
    void countGuideHabits_returnsCount() {
        when(guideHabitRepo.count()).thenReturn(9L);
        assertEquals(9, resolver.countGuideHabits());
    }
}
