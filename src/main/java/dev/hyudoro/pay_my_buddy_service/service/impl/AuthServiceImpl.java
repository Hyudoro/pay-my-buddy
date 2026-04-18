package dev.hyudoro.pay_my_buddy_service.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.hyudoro.pay_my_buddy_service.dto.LoginRequest;
import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.AuthService;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    AuthServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authManager){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
    }

    @Override
    public void register(RegisterRequest request) {
        String email = request.email();
        String password = request.password();
        if(repository.existsByEmail(email)){
            throw new IllegalArgumentException("Email already used");
        }
        String hashedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setHashedPassword(hashedPassword);
        newUser.setUsername(request.username());
        repository.save(newUser);
    }

    @Override
    public void login(LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        SecurityContextHolder// static, manages a thread-local context.
            .getContext()
            .setAuthentication(
                authManager
                .authenticate(authManager.authenticate(token)));
    }
}
