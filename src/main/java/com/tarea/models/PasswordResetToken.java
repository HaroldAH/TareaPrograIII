 
package com.tarea.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="password_reset_token", indexes = {
        @Index(name="idx_prt_user_expires", columnList="user_id, expires_at")
}, uniqueConstraints = @UniqueConstraint(name="uq_password_reset_token", columnNames="token"))
public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="token", nullable=false, length=128)
    private String token;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="used", nullable=false)
    private boolean used = false;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt = Instant.now();

     
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public Instant getCreatedAt() { return createdAt; }
}
