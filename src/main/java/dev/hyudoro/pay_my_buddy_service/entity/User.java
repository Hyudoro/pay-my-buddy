package dev.hyudoro.pay_my_buddy_service.entity;

import java.util.UUID;

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

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "email", length = 255, nullable = false, unique = true) // constraint on the ORM lvl
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;


    protected User(){
    }

    // Hibernate generates the id when persisting. not setter needed for Id.
    public UUID getId(){ return id; }

    public String getUsername(){ return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getPasswordHash(){ return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }


}
