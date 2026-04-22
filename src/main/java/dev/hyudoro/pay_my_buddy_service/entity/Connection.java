package dev.hyudoro.pay_my_buddy_service.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name = "connections")
public class Connection {

    @EmbeddedId
    private ConnectionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("connectionUserId")
    @JoinColumn(name = "connection_user_id")
    private User connectedUser;

    protected Connection () {} // for JPA

    public Connection (User user, User connectedUser) {
        this.user = user;
        this.connectedUser = connectedUser;
        this.id = new ConnectionId(user.getId(), connectedUser.getId());
    }

    public ConnectionId getId() { return id;}
    public User getUser() { return user; }
    public User getConnectedUser () { return connectedUser; }

    //No setters, a connection is immutable.
}
