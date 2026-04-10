package dev.hyudoro.pay_my_buddy_service.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.hyudoro.pay_my_buddy_service.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
