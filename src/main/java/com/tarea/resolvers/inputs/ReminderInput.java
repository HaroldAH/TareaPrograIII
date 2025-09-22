// com.tarea.resolvers.inputs.ReminderInput
package com.tarea.resolvers.inputs;

public class ReminderInput {
    private Long id;
    private Long userId;     // 👈 NUEVO
    private Long habitId;
    private String time;      // "HH:mm"
    private String frequency; // "DAILY", "WEEKLY", ...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }        // 👈 NUEVO
    public void setUserId(Long userId) { this.userId = userId; } // 👈 NUEVO

    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}
