package dev.hyudoro.pay_my_buddy_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // The DEFAULT gen_random_uuid() is a DB-side fallback.
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(columnDefinition = "citext", nullable = false, unique = true) // constraint at the ORM lvl
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User(){  //jpa.
    }

    // Hibernate generates the id when persisting. not setter needed for Id.
    public UUID getId(){ return id; }

    public String getUsername(){ return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email.toLowerCase(); }

    public String getHashedPassword(){ return passwordHash; }
    public void setHashedPassword(String passwordHash) { this.passwordHash = passwordHash; }

    public BigDecimal getBalance(){ return balance; }
    public void setBalance(BigDecimal balance){ this.balance = balance; }

    public LocalDateTime getDateCreation(){ return createdAt; }

    //   @PrePersist
    //protected void onCreate(){
    //   this.createdAt =LocalDateTime.now();
    //}
}
