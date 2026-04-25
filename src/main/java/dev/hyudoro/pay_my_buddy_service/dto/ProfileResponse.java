package dev.hyudoro.pay_my_buddy_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfileResponse(
    String username,
    String email,
    BigDecimal balance,
    LocalDateTime createdAt
    )

{}
