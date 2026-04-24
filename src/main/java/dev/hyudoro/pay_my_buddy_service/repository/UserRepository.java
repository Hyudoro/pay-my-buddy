package dev.hyudoro.pay_my_buddy_service.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.hyudoro.pay_my_buddy_service.entity.User;
import jakarta.persistence.LockModeType;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id =:id")
    Optional<User> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.email =:email")
    Optional<User> findByEmailForUpdate(@Param("email") String email);

}
