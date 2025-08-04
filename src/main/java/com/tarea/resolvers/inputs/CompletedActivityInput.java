package com.tarea.resolvers.inputs;

public class CompletedActivityInput {
    private Long id;
    private Long progressLogId;
    private Long habitId;
    private String completedAt;
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProgressLogId() {
        return progressLogId;
    }

    public void setProgressLogId(Long progressLogId) {
        this.progressLogId = progressLogId;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}