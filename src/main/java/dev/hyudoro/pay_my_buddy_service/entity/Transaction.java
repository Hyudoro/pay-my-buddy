package dev.hyudoro.pay_my_buddy_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid" , nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column
    private String description;

    @Column(nullable = false, precision = 15, scale = 2) //We enforce the rule at the ORM lvl.
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //@PrePersist
    //private void prePersist(){
    //    this.createdAt = LocalDateTime.now();
    //}

    protected Transaction() {} //JPA

    public Transaction(User sender, User receiver, String description, BigDecimal amount){
        this.sender = sender;
        this.receiver = receiver;
        this.description = description;
        this.amount = amount;
    }

    public UUID getId() { return id; }

    public User getSender() { return sender; }

    public User getReceiver() { return receiver; }

    public String getDescription() { return description; }

    public BigDecimal getAmount(){ return amount; }

    public LocalDateTime getCreatedAt(){ return createdAt; }
}
