package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hyudoro.pay_my_buddy_service.dto.LoginRequest;
import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    public ResponseEntity<Void> login(HttpServletRequest httpRequest,
                                      HttpServletResponse httpResponse,
                                      @RequestBody @Valid LoginRequest request){
        service.login(request);
        HttpSessionSecurityContextRepository sessioncookie = new HttpSessionSecurityContextRepository();
        sessioncookie.saveContext(SecurityContextHolder.getContext(),httpRequest,httpResponse);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        handler.logout(request, response, auth);
        return ResponseEntity.noContent().build();
    }

}
