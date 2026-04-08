package dev.hyudoro.pay_my_buddy_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.hyudoro.pay_my_buddy_service.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
