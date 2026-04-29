package dev.hyudoro.pay_my_buddy_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Positive;

public record TransactionRequest(
    UUID receiverId,
    @Positive(message = "Wrong amount") BigDecimal amount,
    String description
    )
{}
