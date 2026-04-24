package dev.hyudoro.pay_my_buddy_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
    UUID receiverId,
    BigDecimal amount,
    String description
    )
{}
