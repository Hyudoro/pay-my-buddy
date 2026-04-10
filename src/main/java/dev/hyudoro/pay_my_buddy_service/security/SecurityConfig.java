package dev.hyudoro.pay_my_buddy_service.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http // Disable CSRF
            .csrf(csrf -> csrf.disable())
             // Defining which routes are public and protected.
            .authorizeHttpRequests(auth -> auth
                                   .requestMatchers("/api/auth/**").permitAll()
                                   .anyRequest().authenticated()
           )
            // Session management, spring creates/reads HttpSession.
            .sessionManagement(session -> session
                               .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
