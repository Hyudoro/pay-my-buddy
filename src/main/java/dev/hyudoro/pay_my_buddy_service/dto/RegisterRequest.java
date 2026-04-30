package dev.hyudoro.pay_my_buddy_service.dto;

public record RegisterRequest(
    String username,
    String email,
    String password
)
{}
