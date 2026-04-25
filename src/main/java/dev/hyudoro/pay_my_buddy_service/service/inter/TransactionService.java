package dev.hyudoro.pay_my_buddy_service.service.inter;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserTransactionResponse;

public interface TransactionService  {
    void makeTransaction(TransactionRequest request);
    Page<UserTransactionResponse> listTransaction(Pageable pageable);
}
