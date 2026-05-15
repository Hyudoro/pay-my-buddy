package dev.hyudoro.pay_my_buddy_service.service.impl;
import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserTransactionResponse;
import dev.hyudoro.pay_my_buddy_service.entity.ConnectionId;
import dev.hyudoro.pay_my_buddy_service.entity.Transaction;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.ConnectionNotFoundException;
import dev.hyudoro.pay_my_buddy_service.exception.SelfTransactionException;
import dev.hyudoro.pay_my_buddy_service.exception.SenderInsufficientBalanceException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.ConnectionRepository;
import dev.hyudoro.pay_my_buddy_service.repository.TransactionRepository;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.TransactionService;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final TransactionRepository transactionRepository;


    TransactionServiceImpl(UserRepository userRepository, ConnectionRepository connectionRepository, TransactionRepository transactionRepository){
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository;
        this.transactionRepository = transactionRepository ;
    }

    @Override
    @Transactional
    public void makeTransaction(TransactionRequest request) {
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Transaction attempt from: {} to receiverId: {}, amount: {}", senderEmail, request.receiverId(), request.amount());

        User sender = userRepository.findByEmailForUpdate(senderEmail)
            .orElseThrow(() -> new UserNotFoundException("user not found"));
        UUID senderId = sender.getId();

        if(senderId.equals(request.receiverId())) {
            log.warn("Self-transaction attempt by: {}", senderEmail);
            throw new SelfTransactionException("user cannot send mony to himself");
        }

        User receiver = userRepository.findByIdForUpdate(request.receiverId())
            .orElseThrow(() -> new UserNotFoundException("receiver user not found"));
        UUID receiverId = request.receiverId();

        ConnectionId connection = new ConnectionId(
            (senderId.toString().compareTo(receiverId.toString()) < 0 ? senderId : receiverId),
            (receiverId.toString().compareTo(senderId.toString()) > 0 ? receiverId : senderId));

        if(!connectionRepository.existsById(connection)) {
            log.warn("Transaction rejected, no connection between {} and {}", senderEmail, receiverId);
            throw new ConnectionNotFoundException("connection not found");
        }
        if(sender.getBalance().compareTo(request.amount()) < 0) {
            log.warn("Transaction rejected, insufficient balance for {} (balance: {}, requested: {})", senderEmail, sender.getBalance(), request.amount());
            throw new SenderInsufficientBalanceException("sender's balance is insufficient");
        }

        transactionRepository.save(new Transaction(sender,receiver,request.description(),request.amount()));
        sender.setBalance(sender.getBalance().subtract(request.amount()));
        receiver.setBalance(receiver.getBalance().add(request.amount()));
        log.info("Transaction completed: {} -> {} for amount: {}", senderEmail, receiver.getEmail(), request.amount());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserTransactionResponse> listTransaction(Pageable pageable) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Listing transactions for: {}", userEmail);
        User currentUser = (userRepository.findByEmail(userEmail))
            .orElseThrow(() -> new UserNotFoundException("current user not found"));
        return transactionRepository.findTransactionsOf(currentUser.getId(),pageable);
    }
}
