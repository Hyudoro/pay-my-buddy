package dev.hyudoro.pay_my_buddy_service.service.impl;
import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
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

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

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

        User sender = userRepository.findByEmailForUpdate(senderEmail)
            .orElseThrow(() -> new UserNotFoundException("user not found"));
        UUID senderId = sender.getId();

        if(senderId.equals(request.receiverId())) throw new SelfTransactionException("user cannot send mony to himself");

        User receiver = userRepository.findByIdForUpdate(request.receiverId())
            .orElseThrow(() -> new UserNotFoundException("receiver user not found"));
        UUID receiverId = request.receiverId();

        ConnectionId connection = new ConnectionId(
            (senderId.toString().compareTo(receiverId.toString()) < 0 ? senderId : receiverId),
            (receiverId.toString().compareTo(senderId.toString()) > 0 ? receiverId : senderId));

        if(!connectionRepository.existsById(connection)) throw new ConnectionNotFoundException("connection not found");
        if(sender.getBalance().compareTo(request.amount()) < 0) throw new SenderInsufficientBalanceException("sender's balance is insufficient");

        transactionRepository.save(new Transaction(sender,receiver,request.description(),request.amount()));
        sender.setBalance(sender.getBalance().subtract(request.amount()));
        receiver.setBalance(receiver.getBalance().add(request .amount()));
    }

}
