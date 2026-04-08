package dev.hyudoro.pay_my_buddy_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.hyudoro.pay_my_buddy_service.entity.Connection;
import dev.hyudoro.pay_my_buddy_service.entity.ConnectionId;

public interface ConnectionRepository extends JpaRepository<Connection, ConnectionId>{


}
