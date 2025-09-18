package com.tarea.resolvers.inputs;

public class RoutineHabitInput {
    private Long routineId;
    private Long habitId;
    private Integer orderInRoutine;
    private String targetTimeInRoutine;
    private String notes;

    public Long getRoutineId() { return routineId; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }
    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }
    public Integer getOrderInRoutine() { return orderInRoutine; }
    public void setOrderInRoutine(Integer orderInRoutine) { this.orderInRoutine = orderInRoutine; }
    public String getTargetTimeInRoutine() { return targetTimeInRoutine; }
    public void setTargetTimeInRoutine(String targetTimeInRoutine) { this.targetTimeInRoutine = targetTimeInRoutine; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
