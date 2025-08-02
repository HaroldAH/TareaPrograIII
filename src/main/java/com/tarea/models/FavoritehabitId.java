package com.tarea.rutinas.saludables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
public class FavoritehabitId implements java.io.Serializable {
    private static final long serialVersionUID = -1L;
    @Column(name = "user_id", nullable = false)
    private java.lang.Long userId;

    @Column(name = "habit_id", nullable = false)
    private java.lang.Long habitId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FavoritehabitId entity = (FavoritehabitId) o;
        return java.util.Objects.equals(this.habitId, entity.habitId) &&
                java.util.Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(habitId, userId);
    }

}