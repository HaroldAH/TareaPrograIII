package com.tarea.resolvers;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.tarea.repositories.HabitActivityRepository;
import com.tarea.repositories.GuideRepository;
import com.tarea.repositories.CompletedActivityRepository;

@Controller
public class CountResolver {

    private final HabitActivityRepository habitRepo;
    private final GuideRepository guideRepo;
    private final CompletedActivityRepository completedRepo;

    public CountResolver(
            HabitActivityRepository habitRepo,
            GuideRepository guideRepo,
            CompletedActivityRepository completedRepo) {
        this.habitRepo = habitRepo;
        this.guideRepo = guideRepo;
        this.completedRepo = completedRepo;
    }

    @QueryMapping
    public int countHabitActivities() {
        return (int) habitRepo.count();
    }

    @QueryMapping
    public int countGuides() {
        return (int) guideRepo.count();
    }

    @QueryMapping
    public int countCompletedActivities() {
        return (int) completedRepo.count();
    }
}
