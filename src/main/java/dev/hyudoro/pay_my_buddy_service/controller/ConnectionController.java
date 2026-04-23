package dev.hyudoro.pay_my_buddy_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserConnectionResponse;
import dev.hyudoro.pay_my_buddy_service.service.inter.ConnectionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService service;

    public ConnectionController(ConnectionService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> createConnection(@RequestBody @Valid ConnectionRequest request){
        service.addConnection(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserConnectionResponse>> listUserConnections(){
        return ResponseEntity.ok(service.listConnection()) ;
    }


}
