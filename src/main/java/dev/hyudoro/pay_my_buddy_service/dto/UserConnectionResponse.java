package dev.hyudoro.pay_my_buddy_service.dto;

import java.util.UUID;

public record UserConnectionResponse(
    String username,
    UUID id
    )
{}
