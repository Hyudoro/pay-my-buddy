package dev.hyudoro.pay_my_buddy_service.service.impl;

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
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
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

        if(!GlobalGuard.isAbsent(request.username()))
            user.setUsername(request.username());

        if (!GlobalGuard.isAbsent(request.email())) {
            if (userRepository.existsByEmail(request.email()))
                throw new EmailAlreadyExistsException("email already taken.");
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
        String newPasswordEncoded = passwordEncoder.encode(request.newPassword());
            user.setHashedPassword(newPasswordEncoded);
    }
}
