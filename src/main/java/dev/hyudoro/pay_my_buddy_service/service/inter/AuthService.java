package dev.hyudoro.pay_my_buddy_service.service.inter;

import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;

public interface AuthService{
    void register(RegisterRequest request);
}
