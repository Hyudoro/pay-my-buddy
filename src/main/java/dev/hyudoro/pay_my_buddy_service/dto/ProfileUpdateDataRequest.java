package dev.hyudoro.pay_my_buddy_service.dto;

public record ProfileUpdateDataRequest(
     String username,
     String email,
     String oldPassword,
     String newPassword
    )
{}
