package dev.hyudoro.pay_my_buddy_service.entity;


import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;



@Embeddable
public class ConnectionId implements Serializable{

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "connection_user_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID connectionUserId;

    protected ConnectionId(){} // Empty constructor for JPA (only for compliance avoiding throws at runtime)

    public ConnectionId(UUID userId, UUID connectionUserId){
        this.userId = userId;
        this.connectionUserId = connectionUserId;
    }

    @Override
    public boolean equals(Object o){
        if (this.equals(o)){ return true; }
        if (!(o instanceof ConnectionId that)){ return false; }

        return Objects.equals(userId, that.userId) && Objects.equals(connectionUserId, that.connectionUserId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, connectionUserId);
    }

    public UUID getUserId() { return userId; }
    public UUID getConnectionUserId() { return userId; }
}
