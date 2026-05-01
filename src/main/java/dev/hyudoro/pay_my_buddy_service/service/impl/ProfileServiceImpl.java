package dev.hyudoro.pay_my_buddy_service.service.impl;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdatePasswordRequest;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.EmptyUpdateRequestException;
import dev.hyudoro.pay_my_buddy_service.exception.InvalidPasswordException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    ProfileServiceImpl(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService userDetailsService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Transactional(readOnly = true)
    @Override
    public ProfileResponse showUserData() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
            .orElseThrow(() -> new UserNotFoundException("user not found"));
        return new ProfileResponse(user.getUsername(),
                                   user.getEmail(),
                                   user.getBalance(),
                                   user.getDateCreation());
    }

    @Transactional
    @Override
    public void updateProfile(ProfileUpdateRequest request) {
       if(request.username() == null || request.username().isBlank()
          && (request.email() == null || request.email().isBlank())) throw new EmptyUpdateRequestException("request attributes are all empty");

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException("user not found"));

        if(request.username() != null && !request.username().isBlank()) user.setUsername(request.username());

        if(request.email() != null && !request.email().isBlank()){
            user.setEmail(request.email());
            userRepository.saveAndFlush(user);//important to flush so user.getEmail() actually finds the user's new email's address.
            UserDetails updatedUser = userDetailsService.loadUserByUsername(user.getEmail());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                updatedUser.getPassword(),
                updatedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

        }
    }

    @Transactional
    @Override
    public void updatePassword(ProfileUpdatePasswordRequest request) {
        User user = userRepository.findByEmailForUpdate(SecurityContextHolder.getContext().getAuthentication().getName())
            .orElseThrow(() -> new UserNotFoundException("user not found"));

        if(passwordEncoder.matches(request.oldPassword(),user.getHashedPassword())){
            String newPasswordEncoded = passwordEncoder.encode(request.newPassword());
            user.setHashedPassword(newPasswordEncoded);
        } else {
            throw new InvalidPasswordException("user's password mismatch");
        }
    }
}
