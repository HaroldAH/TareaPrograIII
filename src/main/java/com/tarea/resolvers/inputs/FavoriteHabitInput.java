package com.tarea.resolvers.inputs;

public class FavoriteHabitInput {
    private Long id;
    private Long userId;
    private Long habitId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }
}
