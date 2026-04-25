package dev.hyudoro.pay_my_buddy_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserTransactionResponse(
    String senderUsername,
    String receiverUsername,
    String description,
    BigDecimal amount,
    LocalDateTime createdAt
    )
{}
