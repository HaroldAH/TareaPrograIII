package com.tarea.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "routine_habit")
public class RoutineHabit {

    @EmbeddedId
    private RoutineHabitId id = new RoutineHabitId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("routineId")
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("habitId")
    @JoinColumn(name = "habit_id")
    private Habitactivity habit;

    @Column(name = "order_in_routine")
    private Integer orderInRoutine = 1;

    @Column(name = "target_time_in_routine")
    private java.time.LocalTime targetTimeInRoutine;

    @Column(name = "notes", length = 255)
    private String notes;
}
