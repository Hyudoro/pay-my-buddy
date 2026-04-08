package dev.hyudoro.pay_my_buddy_service.repository;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.hyudoro.pay_my_buddy_service.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {


    @Query("""
           SELECT t
           FROM Transaction t
           WHERE t.sender.id = :userId OR t.receiver.id = :userId
           ORDER BY t.createdAt DESC
        """)
    Page<Transaction> findTransactionsOf(@Param("userId") UUID userId, Pageable pageable);


}
