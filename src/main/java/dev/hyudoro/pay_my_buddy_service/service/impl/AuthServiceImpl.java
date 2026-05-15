package dev.hyudoro.pay_my_buddy_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.hyudoro.pay_my_buddy_service.dto.LoginRequest;
import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.EmailAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.AuthService;

@Service
public class AuthServiceImpl implements AuthService{
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

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
        log.debug("Registration attempt for email: {}", email);
        String password = request.password();
        if(repository.existsByEmail(email)){
            log.warn("Registration failed, email already in use: {}", email);
            throw new EmailAlreadyExistsException("Email already used");
        }
        String hashedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setHashedPassword(hashedPassword);
        newUser.setUsername(request.username());
        repository.save(newUser);
        log.info("User registered successfully: {}", email);
    }

    @Override
    public void login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.email());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        SecurityContextHolder// static, manages a thread-local context.
            .getContext()
            .setAuthentication(
                authManager
                .authenticate(token));
        log.info("User logged in: {}", request.email());
    }
}
