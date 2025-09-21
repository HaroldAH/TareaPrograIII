package com.tarea.resolvers;

import com.tarea.models.Module;
import com.tarea.repositories.*;
import com.tarea.security.SecurityUtils;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CountResolver {

  private final UserRepository userRepo;
  private final HabitActivityRepository habitRepo;
  private final GuideRepository guideRepo;
  private final CompletedActivityRepository completedRepo;
  private final RoutineRepository routineRepo;
  private final ReminderRepository reminderRepo;
  private final FavoriteHabitRepository favoriteRepo;
  private final RoutineHabitRepository routineHabitRepo;
  private final GuideHabitRepository guideHabitRepo;

  public CountResolver(UserRepository userRepo,
                       HabitActivityRepository habitRepo,
                       GuideRepository guideRepo,
                       CompletedActivityRepository completedRepo,
                       RoutineRepository routineRepo,
                       ReminderRepository reminderRepo,
                       FavoriteHabitRepository favoriteRepo,
                       RoutineHabitRepository routineHabitRepo,
                       GuideHabitRepository guideHabitRepo) {
    this.userRepo = userRepo;
    this.habitRepo = habitRepo;
    this.guideRepo = guideRepo;
    this.completedRepo = completedRepo;
    this.routineRepo = routineRepo;
    this.reminderRepo = reminderRepo;
    this.favoriteRepo = favoriteRepo;
    this.routineHabitRepo = routineHabitRepo;
    this.guideHabitRepo = guideHabitRepo;
  }

  /* counts.graphqls */
  @QueryMapping
  public int countHabitActivities() {
    SecurityUtils.requireView(Module.HABITS);
    return (int) habitRepo.count();
  }

  @QueryMapping
  public int countGuides() {
    SecurityUtils.requireView(Module.GUIDES);
    return (int) guideRepo.count();
  }

  @QueryMapping
  public int countCompletedActivities() {
    SecurityUtils.requireView(Module.PROGRESS);
    return (int) completedRepo.count();
  }

  /* pagination.graphqls */
  @QueryMapping
  public int countUsers() {
    SecurityUtils.requireView(Module.USERS);
    return (int) userRepo.count();
  }

  @QueryMapping
  public int countRoutines() {
    SecurityUtils.requireView(Module.ROUTINES);
    return (int) routineRepo.count();
  }

  @QueryMapping
  public int countReminders() {
    SecurityUtils.requireView(Module.REMINDERS);
    return (int) reminderRepo.count();
  }

  @QueryMapping
  public int countFavoriteHabits() {
    SecurityUtils.requireView(Module.HABITS);
    return (int) favoriteRepo.count();
  }

  @QueryMapping
  public int countRoutineHabits() {
    SecurityUtils.requireView(Module.ROUTINES);
    return (int) routineHabitRepo.count();
  }

  @QueryMapping
  public int countGuideHabits() {
    SecurityUtils.requireView(Module.GUIDES);
    return (int) guideHabitRepo.count();
  }
}
