package dev.hyudoro.pay_my_buddy_service.dto;


public record ErrorResponse(
    int status,
    String errorTag,
    String message)
{}
