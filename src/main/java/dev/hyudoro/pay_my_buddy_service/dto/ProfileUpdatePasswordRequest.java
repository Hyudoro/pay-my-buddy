package dev.hyudoro.pay_my_buddy_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdatePasswordRequest
   (
       @NotBlank
       String oldPassword,
       @NotBlank
       String newPassword
   )
{}
