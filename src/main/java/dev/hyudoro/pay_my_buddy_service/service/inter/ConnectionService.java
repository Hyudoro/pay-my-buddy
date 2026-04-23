package dev.hyudoro.pay_my_buddy_service.service.inter;

import java.util.List;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserConnectionResponse;

public interface ConnectionService {
    void addConnection(ConnectionRequest request);
    List<UserConnectionResponse> listConnection();
}
