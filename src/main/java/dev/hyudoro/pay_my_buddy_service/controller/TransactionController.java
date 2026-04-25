package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserTransactionResponse;
import dev.hyudoro.pay_my_buddy_service.service.inter.TransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service){
        this.service = service;
    }

    @GetMapping //createdAt is redundant cause order by timestampz is already given at the db lvl but its a secure net.
    public ResponseEntity<Page<UserTransactionResponse>> listUserTransactions(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(service.listTransaction(pageable));
    }

    @PostMapping //core service.
    public ResponseEntity<Void> makeTransaction(@RequestBody @Valid TransactionRequest request){
        service.makeTransaction(request);
        return ResponseEntity.noContent().build();
    }

}
