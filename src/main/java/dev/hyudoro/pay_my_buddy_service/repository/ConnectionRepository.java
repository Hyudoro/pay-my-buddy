package dev.hyudoro.pay_my_buddy_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.hyudoro.pay_my_buddy_service.entity.Connection;
import dev.hyudoro.pay_my_buddy_service.entity.ConnectionId;
import dev.hyudoro.pay_my_buddy_service.entity.User;

public interface ConnectionRepository extends JpaRepository<Connection, ConnectionId>{

    @Query("""
           SELECT
            CASE
             WHEN c.user.id = :userId
              THEN
                c.connectedUser
              ELSE
                c.user
            END
           FROM Connection c
           WHERE c.user.id = :userId
           OR c.connectedUser.id = :userId
           """)
    List<User> findConnectionsOf(@Param("userId") UUID userId);
}
