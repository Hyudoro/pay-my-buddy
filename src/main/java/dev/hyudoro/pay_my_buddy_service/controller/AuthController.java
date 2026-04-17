package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hyudoro.pay_my_buddy_service.dto.LoginRequest;
import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController{

    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request){
        service.register(request);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request){
        service.login(request);
        return ResponseEntity.ok().build();
    }

}
