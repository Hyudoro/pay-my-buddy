package dev.hyudoro.pay_my_buddy_service.service.inter;

import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;

public interface TransactionService  {
    void makeTransaction(TransactionRequest request);
}
