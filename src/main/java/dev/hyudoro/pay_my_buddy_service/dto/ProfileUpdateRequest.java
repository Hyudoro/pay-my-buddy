package dev.hyudoro.pay_my_buddy_service.dto;

public record ProfileUpdateRequest
    (
        String username,
        String email
    )
{}
