package dev.hyudoro.pay_my_buddy_service.service.inter;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;

public interface ConnectionService {
    void addConnection(ConnectionRequest request);
}
