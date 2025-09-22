package com.tarea.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class GuideHabitId implements Serializable {
    private static final long serialVersionUID = 3564735706616014585L;

    @Column(name = "guide_id", nullable = false)
    private Long guideId;

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

     
    public GuideHabitId() {
    }

     
    public GuideHabitId(Long guideId, Long habitId) {
        this.guideId = guideId;
        this.habitId = habitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GuideHabitId entity = (GuideHabitId) o;
        return Objects.equals(this.guideId, entity.guideId) &&
                Objects.equals(this.habitId, entity.habitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guideId, habitId);
    }
}
