package dev.hyudoro.pay_my_buddy_service.security;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import dev.hyudoro.pay_my_buddy_service.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByEmail(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("No user with email: " + username));

        return  org.springframework.security.core.userdetails.User
               .withUsername(user.getEmail())
               .password(user.getPasswordHash())
               .roles("USER")
               .build();
    }


}
