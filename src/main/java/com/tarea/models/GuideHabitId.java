package com.tarea.rutinas.saludables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
public class GuideHabitId implements java.io.Serializable {
    private static final long serialVersionUID = -1L;
    @Column(name = "guide_id", nullable = false)
    private java.lang.Long guideId;

    @Column(name = "habit_id", nullable = false)
    private java.lang.Long habitId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GuideHabitId entity = (GuideHabitId) o;
        return java.util.Objects.equals(this.guideId, entity.guideId) &&
                java.util.Objects.equals(this.habitId, entity.habitId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(guideId, habitId);
    }

}