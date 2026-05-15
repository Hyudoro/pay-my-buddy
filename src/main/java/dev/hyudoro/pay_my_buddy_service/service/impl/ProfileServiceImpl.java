package dev.hyudoro.pay_my_buddy_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.EmailAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.ProfileService;
import dev.hyudoro.pay_my_buddy_service.service.validation.BasicInfoValidator;
import dev.hyudoro.pay_my_buddy_service.service.validation.GlobalGuard;
import dev.hyudoro.pay_my_buddy_service.service.validation.PasswordValidator;

@Service
public class ProfileServiceImpl implements ProfileService{
    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    ProfileServiceImpl(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService userDetailsService
                       ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Transactional(readOnly = true)
    @Override
    public ProfileResponse showUserData() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Fetching profile data for: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException("user not found."));
        return new ProfileResponse(user.getUsername(),
                                   user.getEmail(),
                                   user.getBalance(),
                                   user.getDateCreation());
    }

    @Transactional
    @Override
    public void updateProfile(ProfileUpdateDataRequest request) {
        String userEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Profile update request for: {}", userEmail);
        User user = userRepository.findByEmailForUpdate(userEmail)
            .orElseThrow(() -> new UserNotFoundException("user not found."));

        GlobalGuard.check(request);

        if(!GlobalGuard.isAbsent(request.username()) || !GlobalGuard.isAbsent(request.email()))
            updateBasicInfo(request,user);


        if(!GlobalGuard.isAbsent(request.oldPassword()) && !GlobalGuard.isAbsent(request.newPassword()))
            updatePassword(request,user);
    }

    private void updateBasicInfo(ProfileUpdateDataRequest request, User user){
        BasicInfoValidator.validate(request,user);

        if(!GlobalGuard.isAbsent(request.username())) {
            log.info("Updating username for user: {}", user.getEmail());
            user.setUsername(request.username());
        }

        if (!GlobalGuard.isAbsent(request.email())) {
            if (userRepository.existsByEmail(request.email())) {
                log.warn("Email update rejected, already taken: {}", request.email());
                throw new EmailAlreadyExistsException("email already taken.");
            }
            log.info("Updating email for user: {} -> {}", user.getEmail(), request.email());
            user.setEmail(request.email());
            userRepository.saveAndFlush(user);
            UserDetails updatedUser = userDetailsService.loadUserByUsername(user.getEmail());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                updatedUser.getPassword(),
                updatedUser.getAuthorities());
           SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    private void updatePassword(ProfileUpdateDataRequest request, User user){
        PasswordValidator.validate(request,user,passwordEncoder);
        log.info("Updating password for user: {}", user.getEmail());
        String newPasswordEncoded = passwordEncoder.encode(request.newPassword());
            user.setHashedPassword(newPasswordEncoded);
    }
}
