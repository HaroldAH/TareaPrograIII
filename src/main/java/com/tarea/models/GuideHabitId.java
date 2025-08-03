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

    private static final long serialVersionUID = -1L;

    @Column(name = "guide_id", nullable = false)
    private Long guideId;

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GuideHabitId that = (GuideHabitId) o;
        return Objects.equals(guideId, that.guideId) &&
               Objects.equals(habitId, that.habitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guideId, habitId);
    }
}
